package com.ziplly.app.model;

import java.util.ArrayList;
import java.util.List;

public enum TweetType {
	ALL,
	GENERAL,
	ANNOUNCEMENT,
	HELP,
	CLASSIFIEDS,
	OFFERS;
	
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
}
