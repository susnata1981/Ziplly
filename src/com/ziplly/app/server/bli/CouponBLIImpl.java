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
import com.ziplly.app.client.exceptions.InternalError;
import com.ziplly.app.client.exceptions.InvalidCouponException;
import com.ziplly.app.dao.CouponTransactionDAO;
import com.ziplly.app.dao.DAOModule;
import com.ziplly.app.model.CouponTransaction;
import com.ziplly.app.model.PurchasedCoupon;
import com.ziplly.app.model.PurchasedCouponStatus;
import com.ziplly.app.model.TransactionStatus;
import com.ziplly.app.server.bli.ServiceModule.CouponRedeemEndpoint;
import com.ziplly.app.server.crypto.CryptoUtil;
import com.ziplly.app.server.guice.DispatchServletModule;
import com.ziplly.app.server.handlers.ZipllyActionHandlerModule;

public class CouponBLIImpl implements CouponBLI {
	private static final String SEPARATOR = ":";
	private final String qrcodeEndpoint;
	private static CryptoUtil cryptoUtil;

	private Logger logger = Logger.getLogger(CouponBLIImpl.class.getName());
	private String REDEEM_TOKEN = "redeem";
	private CouponTransactionDAO couponTransactionDao;
	private String couponRedeemEndpoint;

	@Inject
	public CouponBLIImpl(@Named("qrcode_endpoint") String qrcodeEndpoint,
	    @CouponRedeemEndpoint String couponRedeemEndpoint,
	    CryptoUtil cryptoUtil,
	    CouponTransactionDAO couponTransactionDao) {
		
		this.qrcodeEndpoint = qrcodeEndpoint;
		this.couponRedeemEndpoint = couponRedeemEndpoint;
		System.out.println("CRE="+couponRedeemEndpoint);
		CouponBLIImpl.cryptoUtil = cryptoUtil;
		this.couponTransactionDao = couponTransactionDao;
	}

	@Override
	public String
	    getQrcodeUrl(Long buyerAccountId, Long sellerAccountId, Long couponId) throws InternalError {

		CouponCodeDetails ccd = new CouponCodeDetails();
		ccd.setBuyerAccountId(buyerAccountId)
		    .setSellerAccountId(sellerAccountId)
		    .setCouponId(couponId);
		try {
			return getQrcodeUrl(ccd.getEncryptedCouponCode());
		} catch (Exception ex) {
			logger.severe(String.format("Failed to encrypt message %s", ex));
			throw new InternalError("Failed to encrypt coupon");
		}
	}

	private String getQrcodeUrl(String encryptedCouponCode) throws UnsupportedEncodingException {
		String redeemUrl = couponRedeemEndpoint + encryptedCouponCode;
		return qrcodeEndpoint + URLEncoder.encode(redeemUrl, "utf-8");
	}

	@Deprecated
	public void markAsUsed(String url) throws InvalidKeyException,
	    NoSuchAlgorithmException,
	    NoSuchPaddingException,
	    IllegalBlockSizeException,
	    BadPaddingException,
	    IOException {

		if (url != null) {
			// url = URLDecoder.decode(url, "utf-8");
			// String encryptedText = url.substring(url.indexOf(REDEEM_TOKEN) +
			// REDEEM_TOKEN.length());
			String encryptedText = getEncryptedQRCode(url);
			System.out.println("U=" + url);
			System.out.println("ecrypted text=" + URLDecoder.decode(encryptedText, "utf-8"));
			String decryptText = cryptoUtil.decrypt(URLDecoder.decode(encryptedText, "utf-8"));
			System.out.println("Decrypted text=" + decryptText);
			String[] tokens = decryptText.split(SEPARATOR);
			System.out.println(tokens[0]);
		}
	}

	@Override
	public boolean
	    redeemCoupon(String encodedCouponData, Long buyerAccountId, Long sellerAccountId) throws InvalidCouponException,
	        CouponAlreadyUsedException {

		checkArgument(encodedCouponData != null && buyerAccountId != null && sellerAccountId != null);

		CouponCodeDetails couponCodeDetails;
		try {
			System.out.println("Code="+encodedCouponData);
			couponCodeDetails = CouponCodeDetails.decode(encodedCouponData);
		} catch (Exception e) {
			logger.severe(String.format("Unable to decrypt coupon code %s", encodedCouponData));
			throw new InvalidCouponException(encodedCouponData);
		}

		checkNotNull(couponCodeDetails);

		System.out.println(couponCodeDetails);
		
		// Load CouponTransaction
		CouponTransaction transaction = couponTransactionDao.findById(couponCodeDetails.getCouponId());
		PurchasedCoupon purchasedCoupon = transaction.getPurchasedCoupon();

		if (transaction.getStatus() != TransactionStatus.COMPLETE || purchasedCoupon == null) {
			throw new InvalidCouponException(encodedCouponData);
		}

		if (purchasedCoupon.getStatus() != PurchasedCouponStatus.UNUSED) {
			throw new CouponAlreadyUsedException(encodedCouponData);
		}

		// Otherwise mark the coupon as used
		purchasedCoupon.setStatus(PurchasedCouponStatus.USED);
		purchasedCoupon.setTimeUpdated(new Date());
		couponTransactionDao.update(transaction);
		return true;
	}

	private String getEncryptedQRCode(String url) {
		checkNotNull(url);
		checkArgument(url.indexOf(REDEEM_TOKEN) > 0
		    && url.length() > url.indexOf(REDEEM_TOKEN) + REDEEM_TOKEN.length());
		return url.substring(url.indexOf(REDEEM_TOKEN) + REDEEM_TOKEN.length());
	}

	private static class CouponCodeDetails {
		private Long buyerAccountId;
		private Long sellerAccountId;
		private Long couponId;

		public Long getCouponId() {
			return couponId;
		}

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

			String[] tokens = getUnencryptedTokens(encodedCouponData);
			if (tokens.length != 3) {
				throw new InvalidCouponException(Arrays.toString(tokens));
			}

			CouponCodeDetails ccd = new CouponCodeDetails();
			ccd.setBuyerAccountId(Long.parseLong(tokens[0]));
			ccd.setSellerAccountId(Long.parseLong(tokens[1]));
			ccd.setCouponId(Long.parseLong(tokens[2]));
			return ccd;
		}

		private String encode() {
			return buyerAccountId + SEPARATOR + sellerAccountId + SEPARATOR + couponId;
		}

		private static String[]
		    getUnencryptedTokens(String encodedCouponData) throws InvalidKeyException,
		        NoSuchAlgorithmException,
		        NoSuchPaddingException,
		        IllegalBlockSizeException,
		        BadPaddingException,
		        UnsupportedEncodingException,
		        IOException {
			System.out.println("ecrypted text=" + URLDecoder.decode(encodedCouponData, "utf-8"));
			String decryptText = cryptoUtil.decrypt(URLDecoder.decode(encodedCouponData, "utf-8"));
			System.out.println("Decrypted text=" + decryptText);
			return decryptText.split(SEPARATOR);
		}
		
		@Override
		public String toString() {
			return "BuyerAccountId=" + buyerAccountId + " SellerAcountId=" + sellerAccountId + " CouponId=" + couponId;
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

		String url = URLDecoder.decode(couponBli.getQrcodeUrl(1L, 2L, 3L), "utf-8");
//		String qrcodeEndpoint = "https://chart.googleapis.com/chart?chs=300x300&cht=qr&chl=";
		String redeemUrl = "http://www.ziplly.com/ziplly/redeem?code=";
		System.out.println("Encoded url = " + url);
		String encodedCouponData = url.substring(url.indexOf(redeemUrl) + redeemUrl.length());
		couponBli.redeemCoupon(encodedCouponData, 1L, 2L);
	}
}
