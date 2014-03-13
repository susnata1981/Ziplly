package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

import com.ziplly.app.model.TweetDTO;

public class TweetAction implements Action<TweetResult> {
	private TweetDTO tweet;

	public TweetAction() {
	}

	public TweetAction(TweetDTO tweet) {
		this.setTweet(tweet);
	}

	public TweetDTO getTweet() {
		return tweet;
	}

	public void setTweet(TweetDTO tweet) {
		this.tweet = tweet;
	}
}
