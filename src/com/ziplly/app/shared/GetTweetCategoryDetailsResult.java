package com.ziplly.app.shared;

import java.util.HashMap;
import java.util.Map;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.TweetType;

public class GetTweetCategoryDetailsResult implements Result {
	private Map<TweetType, Integer> tweetCounts = new HashMap<TweetType, Integer>();
	
	public GetTweetCategoryDetailsResult() {
	}

	public Map<TweetType, Integer> getTweetCounts() {
		return tweetCounts;
	}

	public void setTweetCounts(Map<TweetType, Integer> tweetCounts) {
		this.tweetCounts = tweetCounts;
	}
	
	public void setTweetTypeCount(TweetType type, int count) {
		tweetCounts.put(type, count);
	}
}
