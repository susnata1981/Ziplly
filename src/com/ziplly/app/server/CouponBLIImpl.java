package com.ziplly.app.server;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

import com.ziplly.app.client.exceptions.InternalError;
import com.ziplly.app.model.Coupon;

public class CouponBLIImpl implements CouponBLI {

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

	private String encode(Long buyerAccountId, Long sellerAccountId, Long couponId, Long quantityPurchased) throws UnsupportedEncodingException {
		String value = buyerAccountId + "" + sellerAccountId + "" + couponId + "" + quantityPurchased;
		byte [] encodedValue = Base64.encodeBase64(value.getBytes("utf-8"));
		return new String(encodedValue);
  }
}
