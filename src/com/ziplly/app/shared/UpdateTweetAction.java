package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

import com.ziplly.app.model.TweetDTO;

public class UpdateTweetAction implements Action<UpdateTweetResult>{
	private TweetDTO tweet;

	public UpdateTweetAction() {
	}
	
	public UpdateTweetAction(TweetDTO tweet) {
		this.setTweet(tweet);
	}

	public TweetDTO getTweet() {
		return tweet;
	}

	public void setTweet(TweetDTO tweet) {
		this.tweet = tweet;
	}
}
