package com.ziplly.app.model;

import java.util.ArrayList;
import java.util.List;

public enum TweetType {
	ALL("All", NotificationType.NONE),
	GENERAL("General", NotificationType.NONE),
	ANNOUNCEMENT("Announcement", NotificationType.ANNOUNCEMENT),
	SECURITY_ALERTS("Security Alerts", NotificationType.SECURITY_ALERT),
	HELP("Help", NotificationType.NONE),
	CLASSIFIEDS("Classifieds", NotificationType.NONE),
	OFFERS("offers", NotificationType.OFFERS);
	
	private String tweetName;
	private NotificationType notificationType;

	TweetType(String name, NotificationType type) {
		this.setTweetName(name);
		this.setNotificationType(type);
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

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}
}
