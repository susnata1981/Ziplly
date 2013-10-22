package com.ziplly.app.shared;

import com.ziplly.app.model.TweetDTO;

import net.customware.gwt.dispatch.shared.Result;

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
