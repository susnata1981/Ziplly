package com.ziplly.app.server.bli;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.persistence.NoResultException;

import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.apphosting.api.DeadlineExceededException;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.persist.Transactional;
import com.ziplly.app.client.exceptions.CouponAlreadyUsedException;
import com.ziplly.app.client.exceptions.InvalidCouponException;
import com.ziplly.app.dao.PurchasedCouponDAO;
import com.ziplly.app.dao.TransactionDAO;
import com.ziplly.app.model.PurchasedCouponStatus;
import com.ziplly.app.model.TransactionStatus;
import com.ziplly.app.server.bli.ServiceModule.CouponRedeemEndpoint;
import com.ziplly.app.server.crypto.CryptoUtil;
import com.ziplly.app.server.model.jpa.Account;
import com.ziplly.app.server.model.jpa.Coupon;
import com.ziplly.app.server.model.jpa.PurchasedCoupon;
import com.ziplly.app.server.model.jpa.Transaction;
import com.ziplly.app.shared.EmailTemplate;

public class CouponBLIImpl implements CouponBLI {
	private static final String SEPARATOR = ":";
	private static final Long MAX_WAIT_TIME = 4000L;
	private final String qrcodeEndpoint;
	private static CryptoUtil cryptoUtil;

	private Logger logger = Logger.getLogger(CouponBLIImpl.class.getName());
	private TransactionDAO couponTransactionDao;
	private String couponRedeemEndpoint;
	private AccountBLI accountBli;
	private TweetNotificationBLI tweetNotificationBli;
	private PurchasedCouponDAO purchasedCouponDao;

	@Inject
	public CouponBLIImpl(
			AccountBLI accountBli,
			TweetNotificationBLI tweetNotificationBli,
			@Named("qrcode_endpoint") String qrcodeEndpoint,
	    @CouponRedeemEndpoint String couponRedeemEndpoint,
	    CryptoUtil cryptoUtil,
	    PurchasedCouponDAO purchasedCouponDao,
	    TransactionDAO couponTransactionDao) {

		this.accountBli = accountBli;
		this.tweetNotificationBli = tweetNotificationBli;
		this.qrcodeEndpoint = qrcodeEndpoint;
		this.couponRedeemEndpoint = couponRedeemEndpoint;
		CouponBLIImpl.cryptoUtil = cryptoUtil;
		this.purchasedCouponDao = purchasedCouponDao;
		this.couponTransactionDao = couponTransactionDao;
	}

	@Override
	public String getQrcode(PurchasedCoupon pr) throws Exception {
		CouponCodeDetails ccd = new CouponCodeDetails();
		ccd
		    .setBuyerAccountId(pr.getTransaction().getBuyer().getAccountId())
		    .setSellerAccountId(pr.getCoupon().getTweet().getSender().getAccountId())
		    .setCouponId(pr.getCoupon().getCouponId())
		    .setCouponTransactionId(pr.getTransaction().getTransactionId());

		String code = ccd.getEncryptedCouponCode();
		return code;
	}

	@Override
	public String
	    getQrcodeUrl(PurchasedCoupon pr) throws UnsupportedEncodingException {
		String redeemUrl =
		    couponRedeemEndpoint + encode(pr.getQrcode());
		return qrcodeEndpoint + redeemUrl;
	}

	String encode(String code) throws UnsupportedEncodingException {
		checkNotNull(code);
		return URLEncoder.encode(code, "utf-8");
	}

	@Override
	public Coupon
	    redeemCoupon(String encodedCouponData, Long sellerAccountId) throws InvalidCouponException,
	        CouponAlreadyUsedException {

		checkArgument(encodedCouponData != null && sellerAccountId != null);

		CouponCodeDetails couponCodeDetails;
		try {
			couponCodeDetails = CouponCodeDetails.decode(decode(encodedCouponData));
		} catch (Exception e) {
			logger.severe(String.format("Unable to decrypt coupon code %s", encodedCouponData));
			throw new InvalidCouponException(encodedCouponData);
		}

		checkNotNull(couponCodeDetails);

		System.out.println(couponCodeDetails);

		// Load CouponTransaction
		List<PurchasedCoupon> purchasedCoupons = purchasedCouponDao.findByTransactionId(couponCodeDetails.getCouponTransactionId());
		PurchasedCoupon purchasedCoupon = purchasedCoupons.get(0);
		if (purchasedCoupon.getTransaction().getStatus() != TransactionStatus.COMPLETE || purchasedCoupon == null) {
			// TODO(vipin) throw a different error?
			throw new InvalidCouponException(encodedCouponData);
		}

		if (purchasedCoupon.getStatus() == PurchasedCouponStatus.USED) {
			throw new CouponAlreadyUsedException(encodedCouponData);
		}

		// Otherwise mark the coupon as used
		purchasedCoupon.setStatus(PurchasedCouponStatus.USED);
		purchasedCoupon.setTimeUpdated(new Date());
		Coupon coupon = purchasedCoupon.getCoupon();
		coupon.setQuantityPurchased(coupon.getQuantityPurchased() + 1);
		purchasedCouponDao.update(purchasedCoupon);
		return coupon;
	}

	public static class CouponCodeDetails {
		private Long buyerAccountId;
		private Long sellerAccountId;
		private Long couponId;
		private Long couponTransactionId;

		public CouponCodeDetails setCouponId(Long couponId) {
			this.couponId = couponId;
			return this;
		}

		public CouponCodeDetails setSellerAccountId(Long sellerAccountId) {
			this.sellerAccountId = sellerAccountId;
			return this;
		}

		public CouponCodeDetails setBuyerAccountId(Long buyerAccountId) {
			this.buyerAccountId = buyerAccountId;
			return this;
		}

		public CouponCodeDetails setCouponTransactionId(Long transactionId) {
			this.couponTransactionId = transactionId;
			return this;
		}

		/**
		 * Encrypts the encoded coupon code
		 */
		public String getEncryptedCouponCode() throws Exception {
			return cryptoUtil.encrypt(encode());
		}

		/**
		 * Decrypts given coupon code
		 */
		public static CouponCodeDetails decode(String encodedCouponData) throws InvalidCouponException,
		    InvalidKeyException,
		    NoSuchAlgorithmException,
		    NoSuchPaddingException,
		    IllegalBlockSizeException,
		    BadPaddingException,
		    UnsupportedEncodingException,
		    IOException {

			checkNotNull(encodedCouponData);

			String[] tokens = decryptTokens(encodedCouponData);
			if (tokens.length != 4) {
				throw new InvalidCouponException(Arrays.toString(tokens));
			}

			CouponCodeDetails ccd = new CouponCodeDetails();
			ccd.setBuyerAccountId(Long.parseLong(tokens[0]));
			ccd.setSellerAccountId(Long.parseLong(tokens[1]));
			ccd.setCouponId(Long.parseLong(tokens[2]));
			ccd.setCouponTransactionId(Long.parseLong(tokens[3]));
			return ccd;
		}

		public String encode() {
			return buyerAccountId + SEPARATOR + sellerAccountId + SEPARATOR + couponId + SEPARATOR
			    + couponTransactionId;
		}

		private static String[] decryptTokens(String encodedCouponData) throws InvalidKeyException,
		    NoSuchAlgorithmException,
		    NoSuchPaddingException,
		    IllegalBlockSizeException,
		    BadPaddingException,
		    UnsupportedEncodingException,
		    IOException {

			String decryptText = cryptoUtil.decrypt(encodedCouponData);
			System.out.println("Decrypted text=" + decryptText);
			return decryptText.split(SEPARATOR);
		}

		@Override
		public String toString() {
			return "BuyerAccountId=" + buyerAccountId + " SellerAcountId=" + sellerAccountId
			    + " CouponId=" + couponId + " transactionId = " + couponTransactionId;
		}

		public Long getBuyerId() {
			return buyerAccountId;
		}

		public Long getSellerId() {
			return sellerAccountId;
		}

		public Long getCouponId() {
			return couponId;
		}

		public Long getCouponTransactionId() {
			return couponTransactionId;
		}
	}

	@Override
	@Transactional
	public PurchasedCoupon createPendingTransaction(Account buyer, Coupon coupon) {
		Date now = new Date();
		PurchasedCoupon pc = new PurchasedCoupon();
		pc.setCoupon(coupon);
		pc.setStatus(PurchasedCouponStatus.PENDING);
		Transaction transaction = new Transaction();
		transaction.setBuyer(buyer);
		transaction.setStatus(TransactionStatus.PENDING);
		transaction.setAmount(coupon.getPrice());
		transaction.setCurrency(Currency.getInstance(Locale.US).getCurrencyCode());
		transaction.setTimeCreated(now);
		transaction.setTimeUpdated(now);
		pc.setTransaction(transaction);
		pc.setTimeCreated(now);
		pc.setTimeUpdated(now);
		purchasedCouponDao.save(pc);
		return pc;
	}
	
	@Override
	@Transactional
	public void completeTransaction(Long purchaseCouponId) throws Exception {
		PurchasedCoupon purchasedCoupon = purchasedCouponDao.findById(purchaseCouponId);
		checkNotNull(purchasedCoupon);
		checkState(purchasedCoupon.getStatus() == PurchasedCouponStatus.PENDING);
		
		try {
			accountBli.checkAccountEligibleForCouponPurchase(
					purchasedCoupon.getTransaction().getBuyer(), purchasedCoupon.getCoupon());
		} catch(DispatchException ex) {
			purchasedCoupon.setStatus(PurchasedCouponStatus.ELIGIBILITY_FAILED);
			purchasedCoupon.getTransaction().setStatus(TransactionStatus.ELIGIBILITY_FAILED);
			purchasedCouponDao.save(purchasedCoupon);
			throw ex;
		}

		// Update quantity 
		purchasedCoupon.getCoupon().setQuantityPurchased(purchasedCoupon.getCoupon().getQuantityPurchased() + 1);
		
		// Update the status
		Transaction txn = purchasedCoupon.getTransaction();
		txn.setStatus(TransactionStatus.COMPLETE);
		
		// Set update date
		Date now = new Date();
		txn.setTimeUpdated(now);
		
		// Set QR code
		purchasedCoupon.setQrcode(getQrcode(purchasedCoupon));
		purchasedCoupon.setStatus(PurchasedCouponStatus.UNUSED);
		purchasedCoupon.setTimeUpdated(now);
		purchasedCoupon.setTimeCreated(now);
		
		try {
			purchasedCoupon.setQrcode(getQrcode(purchasedCoupon));
			purchasedCouponDao.update(purchasedCoupon);
			tweetNotificationBli.sendCouponPurchaseNotification(txn, EmailTemplate.COUPON_PURCHASE);
		} catch (Exception e) {
			throw new InternalError(String.format("Failed to generate coupon code"));
		}
	}
	
	@Deprecated
	@Override
	public void waitAndCompleteTransaction(Long transactionId) throws InterruptedException {
//		CouponTransaction transaction = couponTransactionDao.findById(transactionId);
		boolean found = false;
		Transaction transaction = null;
		Long waitingTime = 0L;
		
		while (!found) {
			try {
				transaction = couponTransactionDao.findByIdAndStatus(
						transactionId,
				    TransactionStatus.PENDING_COMPLETE);
				found = true;
			} catch (NoResultException nre) {
				waitingTime += 100;
				Thread.sleep(100);
				if (waitingTime > MAX_WAIT_TIME) {
					throw new DeadlineExceededException();
				}
			}
		}
		transaction.setStatus(TransactionStatus.COMPLETE);
		couponTransactionDao.update(transaction);
	}
	
	private String decode(String encodedString) throws UnsupportedEncodingException {
		checkNotNull(encodedString);
		return URLDecoder.decode(encodedString, "utf-8");
	}
}
