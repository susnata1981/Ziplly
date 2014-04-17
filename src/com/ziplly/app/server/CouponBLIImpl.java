package com.ziplly.app.server;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.codec.binary.Base64;

import com.ziplly.app.client.exceptions.InternalError;
import com.ziplly.app.model.Coupon;

public class CouponBLIImpl implements CouponBLI {

	private String QRCODE_URL_ENDPOINT = "https://chart.googleapis.com/chart?chs=300x300&cht=qr&chl=";
	
	@Override
  public String getQrcode(Long buyerAccountId, Coupon coupon) throws InternalError {
		try {
		return encode(
				buyerAccountId, 
				coupon.getTweet().getSender().getAccountId(), 
				coupon.getCouponId(), 
				coupon.getQuantityPurchased());
		} catch(UnsupportedEncodingException ex) {
			throw new InternalError("UnsupportedEncodingException");
		}
  }

	private String encode(
			Long buyerAccountId, 
			Long sellerAccountId, 
			Long couponId, 
			Long quantityPurchased) throws UnsupportedEncodingException {
		
		String value = buyerAccountId + "" + sellerAccountId + "" + couponId + "" + quantityPurchased;
		byte [] encodedValue = Base64.encodeBase64(value.getBytes("utf-8"));
		return new String(encodedValue);
  }
	
	private String getQRCodeUrl(String code) throws UnsupportedEncodingException {
		try {
	    return QRCODE_URL_ENDPOINT + URLEncoder.encode(code, "utf-8");
    } catch (UnsupportedEncodingException e) {
	    throw e;
    }
	}
}
