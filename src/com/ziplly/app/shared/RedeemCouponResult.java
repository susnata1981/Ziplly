package com.ziplly.app.shared;

import com.ziplly.app.model.TweetDTO;

import net.customware.gwt.dispatch.shared.Result;

public class RedeemCouponResult implements Result {
	private TweetDTO tweet;

	public TweetDTO getTweet() {
	  return tweet;
  }

	public void setTweet(TweetDTO tweet) {
	  this.tweet = tweet;
  }
}
