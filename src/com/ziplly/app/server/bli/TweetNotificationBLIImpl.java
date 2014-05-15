package com.ziplly.app.server.bli;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.backends.BackendServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.AccountNotificationDAO;
import com.ziplly.app.dao.DAOModule.BackendAddress;
import com.ziplly.app.dao.EntityUtil;
import com.ziplly.app.dao.NeighborhoodDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountNotification;
import com.ziplly.app.model.AccountNotificationSettingsDTO;
import com.ziplly.app.model.EntityType;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.NotificationAction;
import com.ziplly.app.model.NotificationType;
import com.ziplly.app.model.ReadStatus;
import com.ziplly.app.model.RecordStatus;
import com.ziplly.app.model.Session;
import com.ziplly.app.model.Tweet;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.server.EmailAction;
import com.ziplly.app.server.ZipllyServerConstants;
import com.ziplly.app.shared.EmailTemplate;

public class TweetNotificationBLIImpl implements TweetNotificationBLI {
	Logger logger = Logger.getLogger(TweetNotificationBLIImpl.class.getCanonicalName());

	private ImmutableList<TweetType> emailNotificationTypeList = ImmutableList.of(
	    TweetType.ANNOUNCEMENT,
	    TweetType.SECURITY_ALERTS,
	    TweetType.OFFERS,
	    TweetType.HOT_DEALS);

	private final AccountDAO accountDao;
	private final NeighborhoodDAO neighborhoodDao;
	private final TweetDAO tweetDao;
	private final EmailService emailService;
	private final AccountNotificationDAO accountNotificationDao;
	private final SessionDAO sessionDao;
	private String backendAddress;

	@Inject
	public TweetNotificationBLIImpl(
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    NeighborhoodDAO neighborhoodDao,
	    TweetDAO tweetDao,
	    AccountNotificationDAO accountNotificationDao,
	    EmailService emailService,
	    @BackendAddress String backendAddress) {
		this.accountDao = accountDao;
		this.sessionDao = sessionDao;
		this.neighborhoodDao = neighborhoodDao;
		this.tweetDao = tweetDao;
		this.accountNotificationDao = accountNotificationDao;
		this.emailService = emailService;
		this.backendAddress = backendAddress;
	}

	private boolean shouldSendNotification(TweetDTO tweet) {
		return emailNotificationTypeList.contains(tweet.getType());
	}

	/**
	 * Called by TweetActionHandler
	 * 
	 * @throws NotFoundException
	 */
	@Override
	public void sendNotificationsIfRequired(TweetDTO tweet) throws NotFoundException {
		logger.info(String.format("Received request to send notification %s", tweet));
		if (!shouldSendNotification(tweet)) {
			return;
		}

		EmailTemplate template;
		switch (tweet.getType()) {
			case ANNOUNCEMENT:
				template = EmailTemplate.ANNOUNCEMENT;
				break;
			case HOT_DEALS:
			case OFFERS:
				template = EmailTemplate.OFFER;
			case SECURITY_ALERTS:
			default:
				template = EmailTemplate.SECURITY_ALERT;
		}

		AccountDTO sender = tweet.getSender();
		Session session = sessionDao.findSessionByAccountId(sender.getAccountId());
		Queue queue = QueueFactory.getQueue(ZipllyServerConstants.EMAIL_QUEUE_NAME);

		String mailEndpoint = System.getProperty(ZipllyServerConstants.MAIL_ENDPOINT);

		logger.info(String.format(
		    "Adding notification request to queue %s, backend(0) %s, endpoint %s",
		    queue,
		    backendAddress,
		    mailEndpoint));

		TaskOptions options =
		    TaskOptions.Builder
		        .withUrl(mailEndpoint)
		        .method(Method.POST)
		        .param(ZipllyServerConstants.ACTION_KEY, EmailAction.BY_NEIGHBORHOOD.name())
		        .param(
		            ZipllyServerConstants.NEIGHBORHOOD_ID_KEY,
		            Long.toString(session.getLocation().getNeighborhood().getNeighborhoodId()))
		        .param(ZipllyServerConstants.SENDER_ACCOUNT_ID_KEY, sender.getAccountId().toString())
		        .param(ZipllyServerConstants.TWEET_ID_KEY, tweet.getTweetId().toString())
		        .param(
		            ZipllyServerConstants.NOTIFICATION_TYPE_KEY,
		            tweet.getType().getNotificationType().name())
		        .param(ZipllyServerConstants.EMAIL_TEMPLATE_ID_KEY, template.name())
		        .header(ZipllyServerConstants.HOST_KEY, backendAddress);
		queue.add(options);
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
	public void sendNotification(Long senderAccountId,
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

	private void sendAccountNotifications(Account sender,
	    List<AccountDTO> recipients,
	    TweetDTO tweet,
	    NotificationType ntype) {

		for (AccountDTO account : recipients) {
			AccountNotification an = new AccountNotification();
			an.setRecipient(EntityUtil.convert(account));
			an.setSender(sender);
			an.setReadStatus(ReadStatus.UNREAD);
			an.setType(ntype);
			an.setStatus(RecordStatus.ACTIVE);
			an.setTimeCreated(new Date());
			an.setTimeUpdated(new Date());
			an.setTweet(new Tweet(tweet));
			accountNotificationDao.save(an);
		}
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
				    0, // start
				    // index
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

	private List<AccountDTO> filterRecipientsByNotificationAction(final List<AccountDTO> recipients,
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
	public void sendEmail(Account sender, Account receiver, EmailTemplate template) {
		Queue queue = QueueFactory.getQueue(ZipllyServerConstants.EMAIL_QUEUE_NAME);
		String backendAddress =
		    BackendServiceFactory.getBackendService().getBackendAddress(
		        System.getProperty(ZipllyServerConstants.BACKEND_INSTANCE_NAME_1));
		String mailEndpoint = System.getProperty(ZipllyServerConstants.MAIL_ENDPOINT);

		TaskOptions options =
		    TaskOptions.Builder
		        .withUrl(mailEndpoint)
		        .method(Method.POST)
		        .param(ZipllyServerConstants.ACTION_KEY, EmailAction.INDIVIDUAL.name())
		        .param(ZipllyServerConstants.RECIPIENT_EMAIL_KEY, receiver.getEmail())
		        .param(ZipllyServerConstants.RECIPIENT_NAME_KEY, receiver.getName())
		        .param(ZipllyServerConstants.SENDER_NAME_KEY, sender.getName())
		        .param(ZipllyServerConstants.SENDER_EMAIL_KEY, sender.getEmail())
		        .param("emailTemplateId", template.name())
		        .header("Host", backendAddress);
		queue.add(options);
	}
}
