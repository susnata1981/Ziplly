package com.ziplly.app.server.bli;

import java.io.UnsupportedEncodingException;

import net.customware.gwt.dispatch.shared.DispatchException;

import com.ziplly.app.client.exceptions.CouponAlreadyUsedException;
import com.ziplly.app.client.exceptions.InvalidCouponException;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.Coupon;
import com.ziplly.app.model.PurchasedCoupon;

public interface CouponBLI {
	
	/**
	 * Returns the QRCode
	 * @throws Exception 
	 */
	String getQrcode(PurchasedCoupon pr) throws Exception;
	
	/**
	 * Returns the QRCode url 
	 * @throws UnsupportedEncodingException 
	 */
	String getQrcodeUrl(PurchasedCoupon pr) throws UnsupportedEncodingException;
	
	/**
	 * Redeems the coupon 
	 */
	Coupon redeemCoupon(String encodedCouponData, Long sellerAccountId) throws InvalidCouponException, CouponAlreadyUsedException;

	void waitAndCompleteTransaction(Long transactionId) throws InterruptedException;

	void completeTransaction(Long purchasedCouponId) throws DispatchException, Exception;

	PurchasedCoupon createPendingTransaction(Account buyer, Coupon coupon);
}
