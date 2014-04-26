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

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.ziplly.app.client.exceptions.CouponAlreadyUsedException;
import com.ziplly.app.client.exceptions.InvalidCouponException;
import com.ziplly.app.dao.CouponTransactionDAO;
import com.ziplly.app.dao.DAOModule;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.Coupon;
import com.ziplly.app.model.CouponTransaction;
import com.ziplly.app.model.PurchasedCoupon;
import com.ziplly.app.model.PurchasedCouponStatus;
import com.ziplly.app.model.TransactionStatus;
import com.ziplly.app.model.Tweet;
import com.ziplly.app.server.bli.ServiceModule.CouponRedeemEndpoint;
import com.ziplly.app.server.crypto.CryptoUtil;
import com.ziplly.app.server.guice.DispatchServletModule;
import com.ziplly.app.server.handlers.ZipllyActionHandlerModule;

public class CouponBLIImpl implements CouponBLI {
	private static final String SEPARATOR = ":";
	private final String qrcodeEndpoint;
	private static CryptoUtil cryptoUtil;

	private Logger logger = Logger.getLogger(CouponBLIImpl.class.getName());
	private CouponTransactionDAO couponTransactionDao;
	private String couponRedeemEndpoint;

	@Inject
	public CouponBLIImpl(@Named("qrcode_endpoint") String qrcodeEndpoint,
	    @CouponRedeemEndpoint String couponRedeemEndpoint,
	    CryptoUtil cryptoUtil,
	    CouponTransactionDAO couponTransactionDao) {
		
		this.qrcodeEndpoint = qrcodeEndpoint;
		this.couponRedeemEndpoint = couponRedeemEndpoint;
		CouponBLIImpl.cryptoUtil = cryptoUtil;
		this.couponTransactionDao = couponTransactionDao;
	}

	@Override
	public String getQrcode(CouponTransaction couponTransaction) throws Exception {
		CouponCodeDetails ccd = new CouponCodeDetails();
		ccd.setBuyerAccountId(couponTransaction.getBuyer().getAccountId())
		    .setSellerAccountId(couponTransaction.getCoupon().getTweet().getSender().getAccountId())
		    .setCouponId(couponTransaction.getCoupon().getCouponId())
		    .setCouponTransactionId(couponTransaction.getTransactionId());
		
		String code = ccd.getEncryptedCouponCode();
		return code;
	}
	
	@Override
	public String getQrcodeUrl(CouponTransaction couponTransaction) throws UnsupportedEncodingException {
		String redeemUrl = couponRedeemEndpoint + encode(couponTransaction.getPurchasedCoupon().getQrcode());
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
		CouponTransaction transaction = couponTransactionDao.findById(couponCodeDetails.getCouponTransactionId());
		PurchasedCoupon purchasedCoupon = transaction.getPurchasedCoupon();

		if (transaction.getStatus() != TransactionStatus.COMPLETE || purchasedCoupon == null) {
			//TODO(vipin) throw a different error?
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
			return buyerAccountId + SEPARATOR + sellerAccountId + SEPARATOR + couponId + SEPARATOR + couponTransactionId;
		}

		private static String[]
		    decryptTokens(String encodedCouponData) throws InvalidKeyException,
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
			return "BuyerAccountId=" + buyerAccountId + " SellerAcountId=" + sellerAccountId + " CouponId=" + couponId + " transactionId = " + couponTransactionId;
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

	public static void main(String[] args) throws Exception {
		// System.out.println(CouponBLIImpl.encode(1L, 2L, 3L));
		// String value = "This is Shaan, This is Shaan Basak";
		// byte[] resp = Base64.encodeBase64(value.getBytes("utf-8"));
		// System.out.println(new String(resp));
		// byte[] result = Base64.decodeBase64(resp);
		// System.out.println(new String(result));

		Injector injector =
		    Guice.createInjector(
		        new DispatchServletModule(),
		        new ZipllyActionHandlerModule(),
		        new DAOModule(),
		        new ServiceModule());

		CouponBLI couponBli = injector.getInstance(CouponBLI.class);
		CouponTransaction ct = new CouponTransaction();
		ct.setTransactionId(48L);
		Account buyer = new Account();
		buyer.setAccountId(1L);
		Account seller = new Account();
		seller.setAccountId(1L);
		Tweet t = new Tweet();
		t.setSender(seller);
		Coupon c = new Coupon();
		c.setCouponId(2L);
		c.setTweet(t);
		ct.setCoupon(c);
		ct.setBuyer(buyer);
		String qrcode = couponBli.getQrcode(ct);
		System.out.println("QR CODE="+qrcode);
//		String url = URLDecoder.decode(couponBli.getQrcodeUrl(1L, 2L, 3L), "utf-8");
//		String qrcodeEndpoint = "https://chart.googleapis.com/chart?chs=300x300&cht=qr&chl=";
//		String redeemUrl = "http://www.ziplly.com/ziplly/redeem?code=";
//		System.out.println("Encoded url = " + url);
//		String encodedCouponData = url.substring(url.indexOf(redeemUrl) + redeemUrl.length());
//		couponBli.redeemCoupon(encodedCouponData, 1L, 2L);
	
		String t1 = "Wj6y+EEyOsOm12P1azFEVA==";
		String t1e =  "V3SlZzsoKAQE1kMvaa%2Bgyg%3D%3D"; //URLEncoder.encode(t1, "utf-8");
		System.out.println(t1e);
		System.out.println(URLDecoder.decode(t1e, "utf-8"));
		
		System.out.println(CouponCodeDetails.decode(qrcode));
//		System.out.println(CouponCodeDetails.decode("zJCmOUUrQPKwy7SJdLBm6w=="));
	}
}
