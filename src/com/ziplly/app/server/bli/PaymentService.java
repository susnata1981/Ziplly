package com.ziplly.app.server.bli;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.SignatureException;

import com.ziplly.app.model.Coupon;

public interface PaymentService {
	
	/**
	 * For billing merchants
	 * @param sellerId
	 * @param d
	 * @return
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
	String generateSubscriptionToken(Long sellerId, Long subscriptionId, BigDecimal amount) throws InvalidKeyException, SignatureException;
//	String getJWT(Long sellerId, BigDecimal d) throws InvalidKeyException, SignatureException;
	
	/**
	 * For generating coupon purchase token
	 * @param transactionId 
	 * @param coupon
	 * @param buyer
	 * @return
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
	String generateJWTTokenForCoupon(
			Long transactionId, 
			Coupon coupon, 
			Long buyerAccountId) throws InvalidKeyException, SignatureException;

	String deserialize(String tokenString);

	String getIssuer();
}
