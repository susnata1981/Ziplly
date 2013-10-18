package com.ziplly.app.shared;

import com.ziplly.app.model.TweetDTO;

import net.customware.gwt.dispatch.shared.Action;

public class TweetAction implements Action<TweetResult>{
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
