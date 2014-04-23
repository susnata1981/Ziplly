package com.ziplly.app.server.bli;

import com.ziplly.app.client.exceptions.CouponAlreadyUsedException;
import com.ziplly.app.client.exceptions.InternalError;
import com.ziplly.app.client.exceptions.InvalidCouponException;

public interface CouponBLI {
	String getQrcodeUrl(Long buyerAccountId, Long sellerAccountId, Long couponId) throws InternalError;
	
	boolean redeemCoupon(String encodedCouponData, Long buyerAccountId, Long sellerAccountId) throws InvalidCouponException, CouponAlreadyUsedException;
}
