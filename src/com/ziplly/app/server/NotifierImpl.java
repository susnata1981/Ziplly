package com.ziplly.app.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.AccountNotificationDAO;
import com.ziplly.app.dao.EntityUtil;
import com.ziplly.app.dao.NeighborhoodDAO;
import com.ziplly.app.dao.SubscriptionPlanDAO;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountNotificationSettingsDTO;
import com.ziplly.app.model.EntityType;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.NotificationAction;
import com.ziplly.app.model.NotificationType;
import com.ziplly.app.model.ReadStatus;
import com.ziplly.app.model.RecordStatus;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.server.bli.EmailService;
import com.ziplly.app.server.bli.EmailServiceImpl;
import com.ziplly.app.server.bli.EmailServiceImpl.EmailEntity;
import com.ziplly.app.server.model.jpa.Account;
import com.ziplly.app.server.model.jpa.AccountNotification;
import com.ziplly.app.server.model.jpa.SubscriptionPlan;
import com.ziplly.app.server.model.jpa.Tweet;
import com.ziplly.app.shared.EmailTemplate;

public class NotifierImpl implements Notifier {
  private final TweetDAO tweetDao;
  private final NeighborhoodDAO neighborhoodDao;
  private final AccountDAO accountDao;
  private final AccountNotificationDAO accountNotificationDao;
  private final SubscriptionPlanDAO subscriptionPlanDao;
  private EmailService emailService;

  private Logger logger = Logger.getLogger(NotifierImpl.class.getName());
  private String adminEmail;
  
  @Inject
  public NotifierImpl(
      AccountDAO accountDao,
      NeighborhoodDAO neighborhoodDao,
      TweetDAO tweetDao,
      AccountNotificationDAO accountNotificationDao,
      EmailService emailService,
      SubscriptionPlanDAO subscriptionPlanDao,
      @Named("admin_email") String adminEmail) {
    
    this.accountDao = accountDao;
    this.neighborhoodDao = neighborhoodDao;
    this.tweetDao = tweetDao;
    this.accountNotificationDao = accountNotificationDao;
    this.emailService = emailService;
    this.subscriptionPlanDao = subscriptionPlanDao;
    this.adminEmail = adminEmail;
  }
  
  @Override
  public void sendNotification(
      Long senderAccountId,
      Long neighborhoodId,
      Long tweetId,
      NotificationType ntype,
      EmailTemplate emailTemplate) {

    Preconditions.checkArgument(
        senderAccountId != null && senderAccountId != null,
        "Invalid argument to sendNotification");

    logger.info(String.format(
        "Received request to send notification - " + "sender %d, " + "neighborhood %d, "
            + "tweetId %d, " + "NotificationType %s, " + "Template %s",
        senderAccountId,
        neighborhoodId,
        tweetId,
        ntype,
        emailTemplate));
    
    try {
      TweetDTO tweet = tweetDao.findTweetById(tweetId);
      Account sender = accountDao.findById(senderAccountId);
      List<AccountDTO> unfilteredRecipients = getAllRecipients(neighborhoodId);

      // Email notification
      List<AccountDTO> emailSubscribers =
          filterRecipientsByNotificationAction(
              unfilteredRecipients,
              tweet.getType(),
              NotificationAction.EMAIL);
      sendEmails(emailSubscribers, sender, emailTemplate);

      // Account notification
      sendAccountNotifications(sender, unfilteredRecipients, tweet, ntype);
    } catch (NotFoundException nfe) {
      logger.log(Level.SEVERE, String.format("Unable to find account with id %d", senderAccountId));
    } catch (NumberFormatException nfe) {
      throw new IllegalArgumentException("Invalid zip passed to sendEmail");
    }
  }
  
  @Override
  public void sendEmail(String recipientEmail,
      String recipientName,
      String senderEmail,
      String senderName,
      EmailTemplate emailTemplate) {

    EmailServiceImpl.Builder builder = new EmailServiceImpl.Builder();
    builder
        .setRecipientName(recipientName)
        .setRecipientEmail(recipientEmail)
        .setSenderName(senderName)
        .setSenderEmail(senderEmail)
        .setEmailTemplate(emailTemplate);

    emailService.sendTemplatedEmailFromSender(builder);
  }
  
  @Override
  public void sendSubscriptionNotification(String recipientEmail, String recipientName, String subscriptionPlanId) {
    String emailFrom = adminEmail;
    EmailEntity from = new EmailEntity();
    from.email = emailFrom;
    EmailEntity to = new EmailEntity();
    to.email = recipientEmail;
    to.name  = recipientName;
    
    SubscriptionPlan plan = subscriptionPlanDao.get(Long.parseLong(subscriptionPlanId));
    Map<String, String> data = new HashMap<String, String>();
    data.put("planName", plan.getName());
    data.put("planDescription", plan.getDescription());
    emailService.sendTemplatedEmail(from, to, EmailTemplate.SUBSCRIPTION_NOTIFICATION, null);
  }
  
  private void sendAccountNotifications(
      Account sender,
      List<AccountDTO> recipients,
      TweetDTO tweet,
      NotificationType ntype) {
    
    List<AccountNotification> notifications = new ArrayList<>();
    for (AccountDTO account : recipients) {
      Date now = new Date();
      AccountNotification an = new AccountNotification();
      an.setRecipient(EntityUtil.convert(account));
      an.setSender(sender);
      an.setReadStatus(ReadStatus.UNREAD);
      an.setType(ntype);
      an.setStatus(RecordStatus.ACTIVE);
      an.setTimeCreated(now);
      an.setTimeUpdated(now);
      an.setTweet(new Tweet(tweet));
      notifications.add(an);
    }
    
    accountNotificationDao.save(notifications);
  }

  private List<AccountDTO> getAllRecipients(Long neighborhoodId) {
    try {
      List<NeighborhoodDTO> neighborhoods =
          neighborhoodDao.findAllDescendentNeighborhoodsIncludingItself(neighborhoodId);

      List<AccountDTO> recipients = Lists.newArrayList();
      for (NeighborhoodDTO neighborhood : neighborhoods) {
        // retrieve upto MAX_VALUE personal accounts (might need to
        // change in future)
        recipients.addAll(accountDao.findAccountsByNeighborhood(
            EntityType.PERSONAL_ACCOUNT,
            neighborhood.getNeighborhoodId(),
            0, // start
            // index
            Integer.MAX_VALUE)); // end index

        recipients.addAll(accountDao.findAccountsByNeighborhood(
            EntityType.BUSINESS_ACCOUNT,
            neighborhood.getNeighborhoodId(),
            0, // start index
            Integer.MAX_VALUE)); // end index
      }

      return recipients;
    } catch (NotFoundException ex) {
      logger.severe(String.format(
          "Unabled to find neighborhood with id %d, exception %s",
          neighborhoodId,
          ex));
    }

    return ImmutableList.of();
  }

  private List<AccountDTO> filterRecipientsByNotificationAction(
      final List<AccountDTO> recipients,
      final TweetType tweetType,
      final NotificationAction naction) {

    Iterable<AccountDTO> recipientsNeedingEmailNotification =
        Iterables.filter(recipients, new Predicate<AccountDTO>() {
          @Override
          public boolean apply(AccountDTO acct) {
            NotificationType notificationType = tweetType.getNotificationType();
            for (AccountNotificationSettingsDTO ans : acct.getNotificationSettings()) {
              if (ans.getType() == notificationType) {
                return ans.getAction() == naction;
              }
            }
            return false;
          }
        });

    return Lists.newArrayList(recipientsNeedingEmailNotification);
  }

  /**
   * Send emails from Sender --> Receiver
   * 
   * @param emailSubscribers
   * @param sender
   * @param emailTemplate
   */
  private void sendEmails(List<AccountDTO> emailSubscribers,
      Account sender,
      EmailTemplate emailTemplate) {

    for (AccountDTO acct : emailSubscribers) {
      sendEmail(
          acct.getEmail(),
          acct.getDisplayName(),
          sender.getEmail(),
          sender.getName(),
          emailTemplate);
    }
  }
}
