package com.ziplly.app.shared;

import com.ziplly.app.model.TweetDTO;

import net.customware.gwt.dispatch.shared.Action;

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
