package com.ziplly.app.server;

import com.ziplly.app.model.TweetDTO;

public interface TweetNotificationBLI {
	boolean shouldSendEmailNotification(TweetDTO tweet);
	boolean shouldNotification(TweetDTO saveTweet);
}
