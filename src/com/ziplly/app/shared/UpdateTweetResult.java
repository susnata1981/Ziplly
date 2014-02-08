package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.TweetDTO;

public class UpdateTweetResult implements Result {
	private TweetDTO tweet;

	public UpdateTweetResult() {
	}

	public void setTweet(TweetDTO tweetDTO) {
		this.tweet = tweetDTO;
	}

	public TweetDTO getTweet() {
		return tweet;
	}
}
