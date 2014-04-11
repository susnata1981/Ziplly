package com.ziplly.app.server;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.List;

import com.google.inject.Inject;
import com.ziplly.app.client.activities.util.CouponUtil;
import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.model.TweetDTO;

public class TweetBLIImpl implements TweetBLI {

	private PaymentService paymentService;

	@Inject
	public TweetBLIImpl(PaymentService paymentService) {
		this.paymentService = paymentService;
  }
	
	@Override
  public void injectJwtToken(List<TweetDTO> tweets) throws InvalidKeyException, SignatureException {
		if (tweets.isEmpty()) {
			return;
		}
		
		for(TweetDTO tweet : tweets) {
			if (tweet.getCoupon() != null) {
				CouponDTO coupon = tweet.getCoupon();
				if (CouponUtil.isActive(coupon)) {
					tweet.setJwtToken(paymentService.getJWTTokenForCoupon(tweet.getCoupon()));
				}
			}
		}
  }
}
