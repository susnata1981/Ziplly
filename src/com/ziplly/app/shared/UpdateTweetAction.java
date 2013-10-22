package com.ziplly.app.shared;

import com.ziplly.app.model.TweetDTO;

import net.customware.gwt.dispatch.shared.Action;

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
