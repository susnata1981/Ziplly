package com.ziplly.app.server;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.ziplly.app.client.exceptions.InternalError;
import com.ziplly.app.dao.DAOModule;
import com.ziplly.app.server.crypto.CryptoUtil;
import com.ziplly.app.server.guice.DispatchServletModule;
import com.ziplly.app.server.guice.ServiceModule;
import com.ziplly.app.server.guice.ServiceModule.QrcodeEndpoint;
import com.ziplly.app.server.guice.ZipllyActionHandlerModule;

public class CouponBLIImpl implements CouponBLI {
	private static final String SEPARATOR = ":";
	private final String qrcodeEndpoint;
	private final CryptoUtil cryptoUtil;

	private Logger logger = Logger.getLogger(CouponBLIImpl.class.getName());
	private String REDEEM_TOKEN = "redeem";
	
	@Inject
	public CouponBLIImpl(@QrcodeEndpoint String qrcodeEndpoint, CryptoUtil cryptoUtil) {
		this.qrcodeEndpoint = qrcodeEndpoint;
		this.cryptoUtil = cryptoUtil;
	}

	@Override
	public String getQrcode(Long buyerAccountId, Long senderAccountId, Long couponId) throws InternalError {
		try {
			String encryptedCouponCode = cryptoUtil.encrypt(encode(
			    buyerAccountId,
			    senderAccountId,
			    couponId));
			
			return getQrcodeUrl(encryptedCouponCode);
		} catch (Exception ex) {
			logger.severe(String.format("Failed to encrypt message %s", ex));
			throw new InternalError("Failed to encrypt coupon");
		}
	}

	private String getQrcodeUrl(String encryptedCouponCode) throws UnsupportedEncodingException {
			String value = "http://www.ziplly.com#redeem:" + encryptedCouponCode;
			return qrcodeEndpoint + URLEncoder.encode(value, "utf-8");
  }

	public static String encode(
			Long buyerAccountId, 
			Long sellerAccountId, 
			Long couponId) throws UnsupportedEncodingException {
		
		String value = buyerAccountId + SEPARATOR + sellerAccountId + SEPARATOR + couponId;
//		System.out.println("Clear text:"+value);
//		byte[] encodedValue = Base64.encodeBase64(value.getBytes("utf-8"));
//		return new String(encodedValue);
		return value;
	}

	@Override
	public void markAsUsed(String url) 
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		
		if (url != null) {
//			url = URLDecoder.decode(url, "utf-8");
//			String encryptedText = url.substring(url.indexOf(REDEEM_TOKEN) + REDEEM_TOKEN.length());
			String encryptedText = getEncryptedQRCode(url);
			System.out.println("U="+url);
			System.out.println("ecrypted text="+URLDecoder.decode(encryptedText, "utf-8"));
			String decryptText = cryptoUtil.decrypt(URLDecoder.decode(encryptedText, "utf-8"));
			System.out.println("Decrypted text="+decryptText);
			String [] tokens = decryptText.split(SEPARATOR);
			System.out.println(tokens[0]);
		}
	}

	public static void main(String[] args) throws InternalError, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
//		System.out.println(CouponBLIImpl.encode(1L, 2L, 3L));
//		String value = "This is Shaan, This is Shaan Basak";
//		byte[] resp = Base64.encodeBase64(value.getBytes("utf-8"));
//		System.out.println(new String(resp));
//		byte[] result = Base64.decodeBase64(resp);
//		System.out.println(new String(result));
		
		Injector injector = Guice.createInjector(
		    new DispatchServletModule(),
		    new ZipllyActionHandlerModule(),
		    new DAOModule(),
		    new ServiceModule());
		
		CouponBLI couponBli = injector.getInstance(CouponBLI.class);
		
		String url = couponBli.getQrcode(1L, 2L, 3L);
		System.out.println("Encoded url = "+url);
		couponBli.markAsUsed(url);
	}

	@Override
  public String getEncryptedQRCode(String url) {
		checkNotNull(url);
		checkArgument(url.indexOf(REDEEM_TOKEN) > 0 && url.length() > url.indexOf(REDEEM_TOKEN) + REDEEM_TOKEN.length());
		return url.substring(url.indexOf(REDEEM_TOKEN) + REDEEM_TOKEN.length());
  }
}
