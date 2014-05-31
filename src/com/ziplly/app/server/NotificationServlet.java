package com.ziplly.app.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.AccountNotificationDAO;
import com.ziplly.app.dao.NeighborhoodDAO;
import com.ziplly.app.dao.PostalCodeDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.model.NotificationType;
import com.ziplly.app.server.bli.EmailService;
import com.ziplly.app.server.bli.EmailServiceImpl.EmailEntity;
import com.ziplly.app.server.bli.TweetNotificationBLI;
import com.ziplly.app.shared.EmailTemplate;

@Singleton
public class NotificationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(NotificationServlet.class.getCanonicalName());
	private EmailService emailService;
	private AccountDAO accountDao;
	private SessionDAO sessionDao;
	private TweetDAO tweetDao;
	private AccountNotificationDAO accountNotificationDao;
	private NeighborhoodDAO neighborhoodDao;
	private TweetNotificationBLI tweetNotificationBli;
	private PostalCodeDAO postalCodeDao;
	private static String APP_ADMING_EMAIL_PROP = "app.admin.email";
	
	@Inject
	public NotificationServlet(
			EmailService emailService,
			TweetDAO tweetDao,
			SessionDAO sessionDao,
			AccountDAO accountDao,
			NeighborhoodDAO neighborhoodDao,
			PostalCodeDAO postalCodeDao,
			AccountNotificationDAO accountNotificationDAO,
			TweetNotificationBLI tweetNotificationBLI) {
		this.emailService = emailService;
		this.postalCodeDao = postalCodeDao;
		this.neighborhoodDao = neighborhoodDao;
		this.accountDao = accountDao;
		this.sessionDao = sessionDao;
		this.tweetDao = tweetDao;
		this.accountNotificationDao = accountNotificationDAO;
		this.tweetNotificationBli = tweetNotificationBLI;
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		
	  String actionString = req.getParameter(ZipllyServerConstants.ACTION_KEY);
		EmailAction action = EmailAction.valueOf(actionString);
		String senderAccountId = req.getParameter(ZipllyServerConstants.SENDER_ACCOUNT_ID_KEY);
		String emailTemplateName = req.getParameter(ZipllyServerConstants.EMAIL_TEMPLATE_ID_KEY);
		EmailTemplate emailTemplate = EmailTemplate.valueOf(emailTemplateName);
		
		logger.log(Level.INFO, String.format(
		    "Received notification request with action %s, sender %s, email template %s",
		    action,
		    senderAccountId,
		    emailTemplate));

		switch (action) {
			case BY_NEIGHBORHOOD:
				String neighborhoodId = req.getParameter(ZipllyServerConstants.NEIGHBORHOOD_ID_KEY);
				String notificationType = req.getParameter(ZipllyServerConstants.NOTIFICATION_TYPE_KEY);
				String tweetId = req.getParameter(ZipllyServerConstants.TWEET_ID_KEY);
				NotificationType ntype = NotificationType.valueOf(notificationType);

				tweetNotificationBli.sendNotification(
				    Long.parseLong(senderAccountId),
				    Long.parseLong(neighborhoodId),
				    Long.parseLong(tweetId),
				    ntype,
				    emailTemplate);

				break;
			case INDIVIDUAL:
				String recipientEmail = req.getParameter(ZipllyServerConstants.RECIPIENT_EMAIL_KEY);
				String recipientName = req.getParameter(ZipllyServerConstants.RECIPIENT_NAME_KEY);
				String senderName = req.getParameter(ZipllyServerConstants.SENDER_NAME_KEY);
				String senderEmail = req.getParameter(ZipllyServerConstants.SENDER_EMAIL_KEY);
				tweetNotificationBli.sendEmail(
				    recipientEmail,
				    recipientName,
				    senderEmail,
				    senderName,
				    emailTemplate);
			case COUPON_TRANSACTION_SUCCESSFUL:
			  recipientEmail = req.getParameter(ZipllyServerConstants.RECIPIENT_EMAIL_KEY);
        recipientName = req.getParameter(ZipllyServerConstants.RECIPIENT_NAME_KEY);
        String emailFrom = System.getProperty(APP_ADMING_EMAIL_PROP, "admin@ziplly.com");
        
        EmailEntity from = new EmailEntity();
        from.email = emailFrom;
        EmailEntity to = new EmailEntity();
        to.email = recipientEmail;
        to.name  = recipientName;
        emailService.sendTemplatedEmail(from, to, EmailTemplate.COUPON_PURCHASE, null);
				break;
		}
		res.setStatus(HttpStatus.SC_OK);
		res.getWriter().println("");
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		logger.log(Level.INFO, String.format("Received _ah/start get call"));
		res.setStatus(HttpStatus.SC_OK);
		res.setHeader("Content-Type", "text/html");
		res.getWriter().println("");
	}
}
