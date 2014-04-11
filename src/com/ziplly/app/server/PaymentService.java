package com.ziplly.app.server;

import java.security.InvalidKeyException;
import java.security.SignatureException;

import com.ziplly.app.model.Account;
import com.ziplly.app.model.Coupon;
import com.ziplly.app.model.CouponDTO;

public interface PaymentService {
	/**
	 * For billing merchants
	 * @param sellerId
	 * @param d
	 * @return
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
	String getJWT(Long sellerId, Double d) throws InvalidKeyException, SignatureException;
	
	/**
	 * For coupon
	 * @param coupon
	 * @param buyer
	 * @return
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
	String getJWTTokenForCoupon(CouponDTO coupon) throws InvalidKeyException, SignatureException;
}
