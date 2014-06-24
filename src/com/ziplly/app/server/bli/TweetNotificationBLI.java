package com.ziplly.app.server.bli;

import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.server.model.jpa.Account;
import com.ziplly.app.server.model.jpa.Subscription;
import com.ziplly.app.server.model.jpa.Transaction;
import com.ziplly.app.server.model.jpa.Tweet;
import com.ziplly.app.shared.EmailTemplate;

public interface TweetNotificationBLI {
  void sendNotificationsIfRequired(Tweet tweet) throws NotFoundException;
  
  void sendEmail(Account sender, Account receiver, EmailTemplate template);

  void sendCouponPurchaseNotification(Transaction txn, EmailTemplate couponPurchase);

  void sendSubscriptionCompletionNotification(Subscription subscription,
      EmailTemplate subscriptionPurchase);
}
