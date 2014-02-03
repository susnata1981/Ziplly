package com.ziplly.app.model;

import java.util.ArrayList;
import java.util.List;

public enum TweetType {
	ALL("All", NotificationType.NONE, false, false),
	GENERAL("General", NotificationType.NONE, true, true),
	ANNOUNCEMENT("Announcement", NotificationType.ANNOUNCEMENT, true, true),
	SECURITY_ALERTS("Security Alerts", NotificationType.SECURITY_ALERT, true, true),
	HELP("Help", NotificationType.NONE, true, true),
	CLASSIFIEDS("Classifieds", NotificationType.NONE, true, false),
	OFFERS("offers", NotificationType.OFFERS, true, true);
	
	private String tweetName;
	private NotificationType notificationType;
	private boolean canUsersPublish;
	private boolean canBusinessPublish;
	
	TweetType(String name, NotificationType type, boolean canUsersPublish, boolean canBusinessPublish) {
		this.tweetName = name;
		this.setNotificationType(type);
		this.setCanUsersPublish(canUsersPublish);
		this.setCanBusinessPublish(canBusinessPublish);
	}
	
	public static List<TweetType> getAllTweetTypeForPublishingByUser() {
		ArrayList<TweetType> types = new ArrayList<TweetType>();
		for(TweetType type : values()) {
			if (type.isCanUsersPublish()) {
				types.add(type);
			}
		}
		return types;
	}
	
	public static List<TweetType> getAllTweetTypeForPublishingByBusiness() {
		ArrayList<TweetType> types = new ArrayList<TweetType>();
		for(TweetType type : values()) {
			if (type.isCanBusinessPublish()) {
				types.add(type);
			}
		}
		return types;
	}

	public String getTweetName() {
		return tweetName;
	}

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}

	public boolean isCanUsersPublish() {
		return canUsersPublish;
	}

	public void setCanUsersPublish(boolean canUsersPublish) {
		this.canUsersPublish = canUsersPublish;
	}

	public boolean isCanBusinessPublish() {
		return canBusinessPublish;
	}

	public void setCanBusinessPublish(boolean canBusinessPublish) {
		this.canBusinessPublish = canBusinessPublish;
	}
}
