package com.ziplly.app.server.bli;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.AccountNotificationDAO;
import com.ziplly.app.dao.EntityUtil;
import com.ziplly.app.dao.NeighborhoodDAO;
import com.ziplly.app.dao.SessionDAO;
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
import com.ziplly.app.server.EmailAction;
import com.ziplly.app.server.TaskSystemHelper;
import com.ziplly.app.server.model.jpa.Account;
import com.ziplly.app.server.model.jpa.AccountNotification;
import com.ziplly.app.server.model.jpa.Session;
import com.ziplly.app.server.model.jpa.Subscription;
import com.ziplly.app.server.model.jpa.Transaction;
import com.ziplly.app.server.model.jpa.Tweet;
import com.ziplly.app.shared.EmailTemplate;

// TODO(susnata): rewrite this class
// Refactor this - do all of this from NotificationServlet.
public class TweetNotificationBLIImpl implements TweetNotificationBLI {
	Logger logger = Logger.getLogger(TweetNotificationBLIImpl.class.getCanonicalName());

	private final AccountDAO accountDao;
	private final NeighborhoodDAO neighborhoodDao;
	private final TweetDAO tweetDao;
	private final EmailService emailService;
	private final AccountNotificationDAO accountNotificationDao;
	private final SessionDAO sessionDao;
  private List<TweetType> emailNotificationTypeList;

  private TaskSystemHelper taskSystemHelper;

	@Inject
	public TweetNotificationBLIImpl(
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    NeighborhoodDAO neighborhoodDao,
	    TweetDAO tweetDao,
	    AccountNotificationDAO accountNotificationDao,
	    EmailService emailService,
	    TaskSystemHelper taskSystemHelper) {
		this.accountDao = accountDao;
		this.sessionDao = sessionDao;
		this.neighborhoodDao = neighborhoodDao;
		this.tweetDao = tweetDao;
		this.accountNotificationDao = accountNotificationDao;
		this.taskSystemHelper = taskSystemHelper;
		this.emailService = emailService;
		emailNotificationTypeList = TweetType.getTweetTypesForRequiringNotification();
	}

	private boolean shouldSendNotification(Tweet tweet) {
		return emailNotificationTypeList.contains(tweet.getType());
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
	 * Sends notification to subscribers in a given neighborhood. If neighborhood
	 * is parent neighborhood, then it notifies residents in all child
	 * neighborhoods otherwise just the residents of child neighborhoods.
	 * 
	 * This will be called by the servlet. It sends 1. Email notification to
	 * subscribers 2. Account notification to every applicable resident
	 */
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

	/**
	 * Called by Notification Servlet
	 */
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

	/**
	 * Called by SendMessageActionHandler
	 */
	@Override
	public void sendEmail(Account sender, Account recipient, EmailTemplate template) {
//		Queue queue = QueueFactory.getQueue(ZipllyServerConstants.EMAIL_QUEUE_NAME);
//		String backendAddress =
//		    BackendServiceFactory.getBackendService().getBackendAddress(
//		        System.getProperty(ZipllyServerConstants.BACKEND_INSTANCE_NAME_1));
//		String mailEndpoint = System.getProperty(ZipllyServerConstants.MAIL_ENDPOINT);
//
//		TaskOptions options =
//		    TaskOptions.Builder
//		        .withUrl(mailEndpoint)
//		        .method(Method.POST)
//		        .param(ZipllyServerConstants.ACTION_KEY, EmailAction.INDIVIDUAL.name())
//		        .param(ZipllyServerConstants.RECIPIENT_EMAIL_KEY, receiver.getEmail())
//		        .param(ZipllyServerConstants.RECIPIENT_NAME_KEY, receiver.getName())
//		        .param(ZipllyServerConstants.SENDER_NAME_KEY, sender.getName())
//		        .param(ZipllyServerConstants.SENDER_EMAIL_KEY, sender.getEmail())
//		        .param("emailTemplateId", template.name())
//		        .header("Host", backendAddress);
//		queue.add(options);
		
		taskSystemHelper.addTask(EmailAction.INDIVIDUAL, 
		    recipient.getName(), 
		    recipient.getEmail(), 
		    sender.getName(), 
		    sender.getEmail(), 
		    template);
	}

  @Override
  public void sendCouponPurchaseNotification(Transaction txn, EmailTemplate couponPurchase) {
//    Queue queue = QueueFactory.getQueue(ZipllyServerConstants.EMAIL_QUEUE_NAME);
//    String backendAddress =
//        BackendServiceFactory.getBackendService().getBackendAddress(
//            System.getProperty(ZipllyServerConstants.BACKEND_INSTANCE_NAME_1));
//    String mailEndpoint = System.getProperty(ZipllyServerConstants.MAIL_ENDPOINT);
//
//    TaskOptions options =
//        TaskOptions.Builder
//            .withUrl(mailEndpoint)
//            .method(Method.POST)
//            .param(ZipllyServerConstants.ACTION_KEY, EmailAction.COUPON_TRANSACTION_SUCCESSFUL.name())
//            .param(ZipllyServerConstants.RECIPIENT_EMAIL_KEY, txn.getBuyer().getEmail())
//            .param(ZipllyServerConstants.RECIPIENT_NAME_KEY, txn.getBuyer().getName())
//            .header("Host", backendAddress);
//    queue.add(options);
    
    Account recipient = txn.getBuyer();
    taskSystemHelper.addTask(
        EmailAction.COUPON_TRANSACTION_SUCCESSFUL, 
        recipient.getName(), 
        recipient.getEmail(), 
        couponPurchase);
  }
  
  @Override
  public void sendSubscriptionCompletionNotification(Subscription subscription, EmailTemplate couponPurchase) {
//    Queue queue = QueueFactory.getQueue(ZipllyServerConstants.EMAIL_QUEUE_NAME);
//    String backendAddress =
//        BackendServiceFactory.getBackendService().getBackendAddress(
//            System.getProperty(ZipllyServerConstants.BACKEND_INSTANCE_NAME_1));
//    String mailEndpoint = System.getProperty(ZipllyServerConstants.MAIL_ENDPOINT);
//
//    TaskOptions options =
//        TaskOptions.Builder
//            .withUrl(mailEndpoint)
//            .method(Method.POST)
//            .param(ZipllyServerConstants.ACTION_KEY, EmailAction.SUBSCRIPTION_NOTFICATION.name())
//            .param(ZipllyServerConstants.RECIPIENT_EMAIL_KEY, subscription.getAccount().getEmail())
//            .param(ZipllyServerConstants.RECIPIENT_NAME_KEY, subscription.getAccount().getName())
//            .param(ZipllyServerConstants.SUBSCRIPTION_PLAN_ID_KEY, subscription.getSubscriptionPlan().getSubscriptionId().toString())
//            .header("Host", backendAddress);
//    queue.add(options);
    
    Account recipient = subscription.getAccount();
    taskSystemHelper.addTask(
        EmailAction.COUPON_TRANSACTION_SUCCESSFUL, 
        recipient.getName(), 
        recipient.getEmail(),
        subscription.getSubscriptionPlan().getSubscriptionId(),
        couponPurchase);
  }
  
}
