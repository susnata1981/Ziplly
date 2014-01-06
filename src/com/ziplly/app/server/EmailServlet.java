package com.ziplly.app.server;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.inject.Singleton;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.AccountDAOImpl;
import com.ziplly.app.dao.AccountNotificationDAO;
import com.ziplly.app.dao.AccountNotificationDAOImpl;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountNotification;
import com.ziplly.app.model.AccountNotificationDTO;
import com.ziplly.app.model.AccountNotificationSettingsDTO;
import com.ziplly.app.model.NotificationAction;
import com.ziplly.app.model.NotificationType;
import com.ziplly.app.model.ReadStatus;
import com.ziplly.app.model.RecordStatus;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.shared.EmailTemplate;

@Singleton
public class EmailServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(EmailServlet.class.getCanonicalName());
	private EmailService emailService;
	private AccountDAO accountDao;
	private AccountNotificationDAO accountNotificationDao;

	public EmailServlet() {
		this.emailService = new EmailServiceImpl();
		this.accountDao = new AccountDAOImpl();
		this.accountNotificationDao = new AccountNotificationDAOImpl();

		// Injector injector = Guice.createInjector(new DAOModule());
		// this.emailService = injector.getInstance(EmailService.class);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		logger.log(Level.INFO, "Received request with email:" + req.getParameter("recipientEmail"));
		String actionString = req.getParameter("action");
		EmailAction action = EmailAction.valueOf(actionString);
		String emailTemplateName = req.getParameter("emailTemplateId");
		EmailTemplate emailTemplate = EmailTemplate.valueOf(emailTemplateName);

		switch (action) {
		case BY_ZIP:
			String zip = req.getParameter("zip");
			String senderAccountId = req.getParameter("senderAccountId");
			String notificationType = req.getParameter("notificationType");
			String tweetId = req.getParameter("tweetId");
			NotificationType ntype = NotificationType.valueOf(notificationType);
			sendNotification(senderAccountId, zip, tweetId, ntype, emailTemplate);
			break;
		case INDIVIDUAL:
			String recipientEmail = req.getParameter("recipientEmail");
			String recipientName = req.getParameter("recipientName");
			sendEmail(recipientEmail, recipientName, emailTemplate);
			break;
		}
		res.setStatus(HttpStatus.SC_OK);
		res.getWriter().println("");
	}

	/**
	 * Sends 2 types of notification
	 * 1. Account notification to everybody in the zip
	 * 2. Email notifications to subscribers in that zip
	 */
	private void sendNotification(
			String senderAccountId, 
			String zip, 
			String tweetId,
			NotificationType ntype,
			EmailTemplate emailTemplate) {
		Preconditions.checkArgument(zip != null && senderAccountId != null,
				"Invalid argument to sendNotification");
		AccountDTO sender = null;
		try {
			int zipCode = Integer.parseInt(zip);
			Long accountId = Long.parseLong(senderAccountId);
			List<AccountDTO> recipients = accountDao.findAllAccountsByZip(zipCode);
			try {
				sender = accountDao.findById(accountId);
				sendNotifications(sender, tweetId, recipients, ntype);
			} catch (NotFoundException nfe) {
				logger.log(Level.SEVERE,
						String.format("Unable to find account with id %d", senderAccountId));
			}

			Iterable<AccountDTO> recipientsNeedingEmailNotification = Iterables.filter(recipients,
					new Predicate<AccountDTO>() {
						@Override
						public boolean apply(AccountDTO acct) {
							try {
								AccountDTO account = accountDao.findById(acct.getAccountId());
								for (AccountNotificationSettingsDTO ans : account
										.getNotificationSettings()) {
									// if (ans.getType() ==
									// NotificationType.SECURITY_ALERT) {
									if (ans.getAction() == NotificationAction.EMAIL) {
										return true;
									}
									return false;
									// }
								}
							} catch (NotFoundException e) {
								e.printStackTrace();
							}
							return true;
						}
					});

			sendEmails(recipientsNeedingEmailNotification, sender, emailTemplate);
		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("Invalid zip passed to sendEmail");
		}
	}

	/**
	 * Send security alert email to every subscribe in a zip code.
	 */
	private void sendEmails(Iterable<AccountDTO> recipientsNeedingEmailNotification,
			AccountDTO sender, EmailTemplate emailTemplate) {
		for (AccountDTO account : recipientsNeedingEmailNotification) {
			sendEmail(account, sender, emailTemplate);
		}
	}

	/**
	 * Sends AccountNotification to a list of user
	 * 
	 * @param sender
	 * @param tweetId 
	 * @param recipients
	 * @param ntype
	 */
	private void sendNotifications(AccountDTO sender, String tweetId, List<AccountDTO> recipients,
			NotificationType ntype) {

		for (AccountDTO account : recipients) {
			AccountNotificationDTO an = new AccountNotificationDTO();
			an.setRecipient(account);
			an.setSender(sender);
			an.setReadStatus(ReadStatus.UNREAD);
			TweetDTO tweet = new TweetDTO();
			tweet.setTweetId(Long.parseLong(tweetId));
			an.setTweet(tweet);
			an.setType(ntype);
			an.setStatus(RecordStatus.ACTIVE);
			an.setTimeCreated(new Date());
			an.setTimeUpdated(new Date());
			accountNotificationDao.save(new AccountNotification(an));
		}
	}

	private void sendEmail(AccountDTO recipient, AccountDTO sender, EmailTemplate emailTemplate) {
		Map<String, String> data = Maps.newHashMap();
		data.put(StringConstants.RECIPIENT_NAME_KEY, recipient.getDisplayName());
		data.put(StringConstants.SENDER_NAME_KEY, sender.getDisplayName());
		data.put(StringConstants.RECIPIENT_EMAIL, recipient.getEmail());
		emailService.sendEmail(data, emailTemplate);
	}

	private void sendEmail(String recipientEmail, String recipientName, EmailTemplate emailTemplate) {
		Map<String, String> data = Maps.newHashMap();
		data.put(StringConstants.RECIPIENT_NAME_KEY, recipientName);
		data.put(StringConstants.SENDER_NAME_KEY, null);
		data.put(StringConstants.RECIPIENT_EMAIL, recipientEmail);
		emailService.sendEmail(data, emailTemplate);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.setStatus(HttpStatus.SC_OK);
		res.setHeader("Content-Type", "text/html");
		res.getWriter().println("");
	}
}
