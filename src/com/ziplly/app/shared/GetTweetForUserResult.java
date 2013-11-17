package com.ziplly.app.shared;

import java.util.List;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.TweetDTO;

public class GetTweetForUserResult implements Result {
	private List<TweetDTO> tweets;

	public GetTweetForUserResult() {
	}

	public GetTweetForUserResult(List<TweetDTO> tweets) {
		this.tweets = tweets;
	}

	public List<TweetDTO> getTweets() {
		return tweets;
	}

	public void setTweets(List<TweetDTO> tweets) {
		this.tweets = tweets;
	}
}
