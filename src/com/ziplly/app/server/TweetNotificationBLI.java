package com.ziplly.app.server;

import com.ziplly.app.model.Account;
import com.ziplly.app.model.NotificationType;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.shared.EmailTemplate;

public interface TweetNotificationBLI {
	void sendNotificationsIfRequired(TweetDTO savedTweet);
	
	void sendNotification(Long senderAccountId, Long neighborhoodId, Long tweetId,
			NotificationType ntype, EmailTemplate emailTemplate);

	void sendEmail(String recipientEmail, 
			String recipientName,
			String senderEmail,
			String senderName,
			EmailTemplate emailTemplate);

	void sendEmail(Account sender, Account receiver, EmailTemplate template);
}
