package com.ziplly.app.shared;

import java.util.ArrayList;
import java.util.List;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.TweetDTO;

public class GetTweetsResult implements Result {
	private Long totalTweetCount;
	private List<TweetDTO> tweets = new ArrayList<TweetDTO>();

	public GetTweetsResult() {
	}

	public List<TweetDTO> getTweets() {
		return tweets;
	}

	public void setTweets(List<TweetDTO> tweets) {
		this.tweets = tweets;
	}

	public Long getTotalTweetCount() {
		return totalTweetCount;
	}

	public void setTotalTweetCount(Long count) {
		this.totalTweetCount = count;
	}
}
