package com.ziplly.app.server.bli;

import java.security.InvalidKeyException;
import java.security.SignatureException;

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
	 * @param transactionId 
	 * @param coupon
	 * @param buyer
	 * @return
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
	String getJWTTokenForCoupon(
			Long transactionId, 
			CouponDTO coupon, 
			Long buyerAccountId) throws InvalidKeyException, SignatureException;

	String deserialize(String tokenString);

	String getIssuer();
}
