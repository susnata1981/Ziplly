package com.ziplly.app.server.bli;

import java.io.UnsupportedEncodingException;

import net.customware.gwt.dispatch.shared.DispatchException;

import com.ziplly.app.client.exceptions.CouponAlreadyUsedException;
import com.ziplly.app.client.exceptions.InvalidCouponException;
import com.ziplly.app.server.model.jpa.Account;
import com.ziplly.app.server.model.jpa.Coupon;
import com.ziplly.app.server.model.jpa.CouponItem;

public interface CouponBLI {
	
	/**
	 * Returns the QRCode
	 * @throws Exception 
	 */
//	String getQrcode(CouponItem pr) throws Exception;
	String getQrcode(long buyerAccountId, long sellerAccountId, long couponId, long orderId) throws Exception;
	
	/**
	 * Returns the QRCode url 
	 * @throws UnsupportedEncodingException 
	 */
	String getQrcodeUrl(CouponItem pr) throws UnsupportedEncodingException;
	
	/**
	 * Redeems the coupon 
	 */
	Coupon redeemCoupon(String encodedCouponData, Long sellerAccountId) throws InvalidCouponException, CouponAlreadyUsedException;

	void completeTransaction(Long purchasedCouponId) throws DispatchException, Exception;

	CouponItem createPendingTransaction(Account buyer, Coupon coupon);
}
