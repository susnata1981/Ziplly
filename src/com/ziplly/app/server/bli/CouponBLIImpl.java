package com.ziplly.app.server.bli;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
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
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.dao.CouponTransactionDAO;
import com.ziplly.app.model.Coupon;
import com.ziplly.app.model.CouponTransaction;
import com.ziplly.app.model.PurchasedCoupon;
import com.ziplly.app.model.PurchasedCouponStatus;
import com.ziplly.app.model.TransactionStatus;
import com.ziplly.app.server.bli.ServiceModule.CouponRedeemEndpoint;
import com.ziplly.app.server.crypto.CryptoUtil;

public class CouponBLIImpl implements CouponBLI {
	private static final String SEPARATOR = ":";
	private static final Long MAX_WAIT_TIME = 4000L;
	private final String qrcodeEndpoint;
	private static CryptoUtil cryptoUtil;

	private Logger logger = Logger.getLogger(CouponBLIImpl.class.getName());
	private CouponTransactionDAO couponTransactionDao;
	private String couponRedeemEndpoint;
	private AccountBLI accountBli;

	@Inject
	public CouponBLIImpl(
			AccountBLI accountBli,
			@Named("qrcode_endpoint") String qrcodeEndpoint,
	    @CouponRedeemEndpoint String couponRedeemEndpoint,
	    CryptoUtil cryptoUtil,
	    CouponTransactionDAO couponTransactionDao) {

		this.accountBli = accountBli;
		this.qrcodeEndpoint = qrcodeEndpoint;
		this.couponRedeemEndpoint = couponRedeemEndpoint;
		CouponBLIImpl.cryptoUtil = cryptoUtil;
		this.couponTransactionDao = couponTransactionDao;
	}

	@Override
	public String getQrcode(CouponTransaction couponTransaction) throws Exception {
		CouponCodeDetails ccd = new CouponCodeDetails();
		ccd
		    .setBuyerAccountId(couponTransaction.getBuyer().getAccountId())
		    .setSellerAccountId(couponTransaction.getCoupon().getTweet().getSender().getAccountId())
		    .setCouponId(couponTransaction.getCoupon().getCouponId())
		    .setCouponTransactionId(couponTransaction.getTransactionId());

		String code = ccd.getEncryptedCouponCode();
		return code;
	}

	@Override
	public String
	    getQrcodeUrl(CouponTransaction couponTransaction) throws UnsupportedEncodingException {
		String redeemUrl =
		    couponRedeemEndpoint + encode(couponTransaction.getPurchasedCoupon().getQrcode());
		return qrcodeEndpoint + redeemUrl;
	}

	String encode(String code) throws UnsupportedEncodingException {
		checkNotNull(code);
		return URLEncoder.encode(code, "utf-8");
	}

	private String decode(String encodedString) throws UnsupportedEncodingException {
		checkNotNull(encodedString);
		return URLDecoder.decode(encodedString, "utf-8");
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
		CouponTransaction transaction =
		    couponTransactionDao.findById(couponCodeDetails.getCouponTransactionId());
		PurchasedCoupon purchasedCoupon = transaction.getPurchasedCoupon();

		if (transaction.getStatus() != TransactionStatus.COMPLETE || purchasedCoupon == null) {
			// TODO(vipin) throw a different error?
			throw new InvalidCouponException(encodedCouponData);
		}

		if (purchasedCoupon.getStatus() == PurchasedCouponStatus.USED) {
			throw new CouponAlreadyUsedException(encodedCouponData);
		}

		// Otherwise mark the coupon as used
		purchasedCoupon.setStatus(PurchasedCouponStatus.USED);
		purchasedCoupon.setTimeUpdated(new Date());
		Coupon coupon = transaction.getCoupon();
		coupon.setQuantityPurchased(coupon.getQuantityPurchased() + 1);
		couponTransactionDao.update(transaction);
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
	public void completeTransaction(Long couponTransactionId) throws DispatchException {
		checkNotNull(couponTransactionId);
		
		CouponTransaction couponTransaction = couponTransactionDao.findCouponTransactionByIdAndStatus(
				Long.valueOf(couponTransactionId), 
				TransactionStatus.PENDING);
		
		try {
			accountBli.checkAccountEligibleForCouponPurchase(
					couponTransaction.getBuyer(), couponTransaction.getCoupon());
		} catch(DispatchException ex) {
			couponTransaction.setStatus(TransactionStatus.ELIGIBILITY_FAILED);
			couponTransactionDao.save(couponTransaction);
			throw ex;
		}

		// Update quantity 
		couponTransaction.getCoupon().setQuantityPurchased(couponTransaction.getCoupon().getQuantityPurchased() + 1);
		
		//update the status
		couponTransaction.setStatus(TransactionStatus.COMPLETE);
		
		// complete purchase in first callback
		boolean completePurchase = Boolean.valueOf(System.getProperty(
				StringConstants.COMPLETE_COUPON_PURCHASE_ON_FIRST_CALLBACK_FLAG, "true"));
		
		logger.info(String.format("CompletePurchase value = %s",Boolean.valueOf(completePurchase).toString()));

//		if(completePurchase) {
//			couponTransaction.setStatus(TransactionStatus.COMPLETE);
//		}
		
		// Set update date
		Date now = new Date();
		couponTransaction.setTimeUpdated(now);
		
		// Set QR code
		PurchasedCoupon purchasedCoupon = new PurchasedCoupon();
		purchasedCoupon.setCouponTransaction(couponTransaction);
		purchasedCoupon.setStatus(PurchasedCouponStatus.UNUSED);
		purchasedCoupon.setTimeUpdated(now);
		purchasedCoupon.setTimeCreated(now);
		couponTransaction.setPurchasedCoupon(purchasedCoupon);
		
		try {
			purchasedCoupon.setQrcode(getQrcode(couponTransaction));
			couponTransactionDao.update(couponTransaction);
		} catch (Exception e) {
			throw new InternalError(String.format("Failed to generate coupon code"));
		}
	}
	
	@Override
	public void waitAndCompleteTransaction(Long transactionId) throws InterruptedException {
//		CouponTransaction transaction = couponTransactionDao.findById(transactionId);
		boolean found = false;
		CouponTransaction transaction = null;
		Long waitingTime = 0L;
		
		while (!found) {
			try {
				transaction = couponTransactionDao.findCouponTransactionByIdAndStatus(
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
}
