package com.ziplly.app.server;

import com.google.common.collect.ImmutableList;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;

public class TweetNotificationBLIImpl implements TweetNotificationBLI {

	private ImmutableList<TweetType> generalNotificationTypeList = ImmutableList.of(
			TweetType.ANNOUNCEMENT,
			TweetType.SECURITY_ALERTS,
			TweetType.OFFERS);

	private ImmutableList<TweetType> emailNotificationTypeList = ImmutableList.of(
			TweetType.ANNOUNCEMENT,
			TweetType.SECURITY_ALERTS);

	@Override
	public boolean shouldSendEmailNotification(TweetDTO tweet) {
		return emailNotificationTypeList.contains(tweet.getType());
	}

	@Override
	public boolean shouldNotification(TweetDTO tweet) {
		return generalNotificationTypeList.contains(tweet.getType());
	}
}
