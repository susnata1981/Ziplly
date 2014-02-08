package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

import com.ziplly.app.model.TweetDTO;

public class BusinessTweetAction implements Action<BusinessTweetResult> {
	private TweetDTO tweet;

	public BusinessTweetAction() {
	}
	
	public BusinessTweetAction(TweetDTO tweet) {
		this.setTweet(tweet);
	}

	public TweetDTO getTweet() {
		return tweet;
	}

	public void setTweet(TweetDTO tweet) {
		this.tweet = tweet;
	}
}
