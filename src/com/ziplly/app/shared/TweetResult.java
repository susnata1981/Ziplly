package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.TweetDTO;

public class TweetResult implements Result {
	private TweetDTO tweet;

	public TweetResult() {
	}
	
	public TweetResult(TweetDTO tweet){
		this.setTweet(tweet);
	}

	public TweetDTO getTweet() {
		return tweet;
	}

	public void setTweet(TweetDTO tweet) {
		this.tweet = tweet;
	}
}
