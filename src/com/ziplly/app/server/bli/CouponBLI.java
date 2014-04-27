package com.ziplly.app.server.bli;

import java.io.UnsupportedEncodingException;

import com.ziplly.app.client.exceptions.CouponAlreadyUsedException;
import com.ziplly.app.client.exceptions.InternalException;
import com.ziplly.app.client.exceptions.InvalidCouponException;
import com.ziplly.app.model.Coupon;
import com.ziplly.app.model.CouponTransaction;

public interface CouponBLI {
	
	/**
	 * Returns the QRCode
	 * @throws Exception 
	 */
	String getQrcode(CouponTransaction couponTransaction) throws InternalException, Exception;
	
	/**
	 * Returns the QRCode url 
	 * @throws UnsupportedEncodingException 
	 */
	String getQrcodeUrl(CouponTransaction couponTransaction) throws UnsupportedEncodingException;
	
	/**
	 * Redeems the coupon 
	 */
	Coupon redeemCoupon(String encodedCouponData, Long sellerAccountId) throws InvalidCouponException, CouponAlreadyUsedException;
}
