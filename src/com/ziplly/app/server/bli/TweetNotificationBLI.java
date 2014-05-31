package com.ziplly.app.server.bli;

import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.NotificationType;
import com.ziplly.app.model.Transaction;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.shared.EmailTemplate;

public interface TweetNotificationBLI {
	void sendNotificationsIfRequired(TweetDTO savedTweet) throws NotFoundException;

	void sendNotification(Long senderAccountId,
	    Long neighborhoodId,
	    Long tweetId,
	    NotificationType ntype,
	    EmailTemplate emailTemplate);

	void sendEmail(String recipientEmail,
	    String recipientName,
	    String senderEmail,
	    String senderName,
	    EmailTemplate emailTemplate);

	void sendEmail(Account sender, Account receiver, EmailTemplate template);

  void sendCouponPurchaseNotification(Transaction txn, EmailTemplate couponPurchase);
}
