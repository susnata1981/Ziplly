package com.ziplly.app.model;

import java.util.ArrayList;
import java.util.List;

public enum TweetType {
	ALL("All"),
	GENERAL("General"),
	ANNOUNCEMENT("Announcement"),
	SECURITY_ALERTS("Security Alerts"),
	HELP("Help"),
	CLASSIFIEDS("Classifieds"),
	OFFERS("offers");
	
	private String tweetName;

	TweetType(String name) {
		this.setTweetName(name);
	}
	
	public static List<TweetType> getAllTweetTypeForPublishingByUser() {
		ArrayList<TweetType> types = new ArrayList<TweetType>();
		for(TweetType tt : values()) {
			if (!tt.equals(ALL)) {
				types.add(tt);
			}
		}
		return types;
	}
	
	public static List<TweetType> getAllTweetTypeForPublishingByBusiness() {
		ArrayList<TweetType> types = new ArrayList<TweetType>();
		types.add(OFFERS);
		types.add(ANNOUNCEMENT);
		return types;
	}

	public String getTweetName() {
		return tweetName;
	}

	public void setTweetName(String tweetName) {
		this.tweetName = tweetName;
	}
}
