package com.ziplly.app.server.bli;

import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.ziplly.app.dao.DAOModule.BackendAddress;
import com.ziplly.app.server.EmailAction;
import com.ziplly.app.server.ZipllyServerConstants;
import com.ziplly.app.server.model.jpa.Tweet;
import com.ziplly.app.shared.EmailTemplate;

public class TaskSystemHelper {
  private final String mailEndpoint;
  private final String backendAddress;
  private final Queue queue;

  private Logger logger = Logger.getLogger(TaskSystemHelper.class.getName());
  
  @Inject
  public TaskSystemHelper(
      @Named("mail_endpoint") String mailEndpoint,
      @BackendAddress String backendAddress) {
    this.mailEndpoint = mailEndpoint;
    this.backendAddress = backendAddress;
    this.queue = QueueFactory.getQueue(ZipllyServerConstants.EMAIL_QUEUE_NAME);
    
    logger.info(String.format("Initializing queue %s, backend(0) %s, endpoint %s", 
        queue,
        backendAddress,
        mailEndpoint));
  }
  
  public void addTask(EmailAction action, long neighborhoodId, Tweet tweet) {
    logger.info(String.format(
        "Sending notification action %s, neighborhood %d, tweet %s",
        action.name(),
        neighborhoodId,
        tweet));
    
    TaskOptions options =
        TaskOptions.Builder
            .withUrl(mailEndpoint)
            .method(Method.POST)
            .param(ZipllyServerConstants.ACTION_KEY, action.name())
            .param(
                ZipllyServerConstants.NEIGHBORHOOD_ID_KEY,
                Long.toString(neighborhoodId))
            .param(ZipllyServerConstants.SENDER_ACCOUNT_ID_KEY, tweet.getSender().getAccountId().toString())
            .param(ZipllyServerConstants.TWEET_ID_KEY, Long.toString(tweet.getTweetId()))
            .param(
                ZipllyServerConstants.NOTIFICATION_TYPE_KEY,
                tweet.getType().getNotificationType().name())
            .param(ZipllyServerConstants.EMAIL_TEMPLATE_ID_KEY, tweet.getType().getTemplate().name())
            .header(ZipllyServerConstants.HOST_KEY, backendAddress);
    queue.add(options);
  }

  public void addTask(
      EmailAction action,
      String recipientName,
      String recipientEmail,
      String senderName,
      String senderEmail,
      EmailTemplate template) {
    
    TaskOptions options =
        TaskOptions.Builder
            .withUrl(mailEndpoint)
            .method(Method.POST)
            .param(ZipllyServerConstants.ACTION_KEY, action.name())
            .param(ZipllyServerConstants.RECIPIENT_EMAIL_KEY, recipientName)
            .param(ZipllyServerConstants.RECIPIENT_NAME_KEY, recipientEmail)
            .param(ZipllyServerConstants.SENDER_NAME_KEY, senderName)
            .param(ZipllyServerConstants.SENDER_EMAIL_KEY, senderEmail)
            .param(ZipllyServerConstants.EMAIL_TEMPLATE_ID_KEY, template.name())
            .header("Host", backendAddress);
    queue.add(options);
    
  }

  public void addTask(
      EmailAction action,
      String recipientName,
      String recipientEmail,
      EmailTemplate couponPurchase) {
    
    TaskOptions options =
        TaskOptions.Builder
            .withUrl(mailEndpoint)
            .method(Method.POST)
            .param(ZipllyServerConstants.ACTION_KEY, action.name())
            .param(ZipllyServerConstants.RECIPIENT_NAME_KEY, recipientName)
            .param(ZipllyServerConstants.RECIPIENT_EMAIL_KEY, recipientEmail)
            .header("Host", backendAddress);
    queue.add(options);
  }

  public void addTask(EmailAction action,
      String recipientName,
      String recipientEmail,
      Long subscriptionId,
      EmailTemplate couponPurchase) {

    TaskOptions options =
        TaskOptions.Builder
            .withUrl(mailEndpoint)
            .method(Method.POST)
            .param(ZipllyServerConstants.ACTION_KEY, action.name())
            .param(ZipllyServerConstants.RECIPIENT_EMAIL_KEY, recipientEmail)
            .param(ZipllyServerConstants.RECIPIENT_NAME_KEY, recipientName)
            .param(ZipllyServerConstants.SUBSCRIPTION_PLAN_ID_KEY, subscriptionId.toString())
            .header("Host", backendAddress);
    queue.add(options);
  }
}
