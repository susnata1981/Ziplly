package com.ziplly.app.server.bli;

import java.util.List;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.server.EmailAction;
import com.ziplly.app.server.model.jpa.Account;
import com.ziplly.app.server.model.jpa.Session;
import com.ziplly.app.server.model.jpa.Subscription;
import com.ziplly.app.server.model.jpa.Transaction;
import com.ziplly.app.server.model.jpa.Tweet;
import com.ziplly.app.shared.EmailTemplate;

public class TweetNotificationBLIImpl implements TweetNotificationBLI {
	private final SessionDAO sessionDao;
  private List<TweetType> emailNotificationTypeList;
  private TaskSystemHelper taskSystemHelper;

  private Logger logger = Logger.getLogger(TweetNotificationBLIImpl.class.getCanonicalName());
	
  @Inject
	public TweetNotificationBLIImpl(
	    SessionDAO sessionDao,
	    EmailService emailService,
	    TaskSystemHelper taskSystemHelper) {
		
    this.sessionDao = sessionDao;
		this.taskSystemHelper = taskSystemHelper;
		this.emailNotificationTypeList = TweetType.getTweetTypesForRequiringNotification();
	}

	/**
	 * Called by TweetActionHandler
	 */
	@Override
	public void sendNotificationsIfRequired(Tweet tweet) throws NotFoundException {
		logger.info(String.format("Received request to send notification %s", tweet));
		if (!shouldSendNotification(tweet)) {
			return;
		}

		Session session = sessionDao.findSessionByAccountId(tweet.getSender().getAccountId());
		
		taskSystemHelper.addTask(
		    EmailAction.BY_NEIGHBORHOOD,
		    session.getLocation().getNeighborhood().getNeighborhoodId().longValue(), 
		    tweet);
	}
	
	/**
	 * Called by SendMessageActionHandler
	 */
	@Override
	public void sendEmail(Account sender, Account recipient, EmailTemplate template) {
		taskSystemHelper.addTask(EmailAction.INDIVIDUAL, 
		    recipient.getName(), 
		    recipient.getEmail(), 
		    sender.getName(), 
		    sender.getEmail(), 
		    template);
	}

  @Override
  public void sendCouponPurchaseNotification(Transaction txn, EmailTemplate couponPurchase) {
    Account recipient = txn.getBuyer();
    taskSystemHelper.addTask(
        EmailAction.COUPON_TRANSACTION_SUCCESSFUL, 
        recipient.getName(), 
        recipient.getEmail(), 
        couponPurchase);
  }
  
  @Override
  public void sendSubscriptionCompletionNotification(Subscription subscription, EmailTemplate couponPurchase) {
    Account recipient = subscription.getAccount();
    taskSystemHelper.addTask(
        EmailAction.COUPON_TRANSACTION_SUCCESSFUL, 
        recipient.getName(), 
        recipient.getEmail(),
        subscription.getSubscriptionPlan().getSubscriptionId(),
        couponPurchase);
  }

  private boolean shouldSendNotification(Tweet tweet) {
    return emailNotificationTypeList.contains(tweet.getType());
  }
}
