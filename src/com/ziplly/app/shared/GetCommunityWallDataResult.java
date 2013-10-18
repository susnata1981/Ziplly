package com.ziplly.app.shared;

import java.util.ArrayList;
import java.util.List;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.TweetDTO;

public class GetCommunityWallDataResult implements Result {
	private List<TweetDTO> tweets = new ArrayList<TweetDTO>();
	
	public GetCommunityWallDataResult() {
	}

	public List<TweetDTO> getTweets() {
		return tweets;
	}

	public void setTweets(List<TweetDTO> tweets) {
		this.tweets = tweets;
	}
}
