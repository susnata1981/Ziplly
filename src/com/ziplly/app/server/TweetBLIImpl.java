package com.ziplly.app.server;

import com.google.inject.Inject;

public class TweetBLIImpl implements TweetBLI {

	private PaymentService paymentService;

	@Inject
	public TweetBLIImpl(PaymentService paymentService) {
		this.paymentService = paymentService;
  }
	
//	@Override
//  public void injectJwtToken(List<TweetDTO> tweets) throws InvalidKeyException, SignatureException {
//		if (tweets.isEmpty()) {
//			return;
//		}
//		
//		for(TweetDTO tweet : tweets) {
//			if (tweet.getCoupon() != null) {
//				CouponDTO coupon = tweet.getCoupon();
//				if (CouponUtil.isActive(coupon)) {
//					tweet.setJwtToken(paymentService.getJWTTokenForCoupon(tweet.getCoupon()));
//				}
//			}
//		}
//  }
}
