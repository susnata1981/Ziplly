package com.ziplly.app.server;

import com.ziplly.app.model.NotificationType;
import com.ziplly.app.shared.EmailTemplate;

public interface Notifier {
  
  public void sendNotification(
      Long senderAccountId,
      Long neighborhoodId,
      Long tweetId,
      NotificationType ntype,
      EmailTemplate emailTemplate);
  
  public void sendEmail(String recipientEmail,
      String recipientName,
      String senderEmail,
      String senderName,
      EmailTemplate emailTemplate);

  void sendSubscriptionNotification(
      String recipientEmail,
      String recipientName,
      String subscriptionPlanId);
}
