package com.ziplly.app.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import com.google.inject.Singleton;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.AccountDAOImpl;
import com.ziplly.app.dao.AccountNotificationDAO;
import com.ziplly.app.dao.AccountNotificationDAOImpl;
import com.ziplly.app.dao.NeighborhoodDAOImpl;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.dao.TweetDAOImpl;
import com.ziplly.app.model.NotificationType;
import com.ziplly.app.shared.EmailTemplate;

@Singleton
public class NotificationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(NotificationServlet.class.getCanonicalName());
	private EmailService emailService;
	private AccountDAO accountDao;
	private TweetDAO tweetDao;
	private AccountNotificationDAO accountNotificationDao;
	private NeighborhoodDAOImpl neighborhoodDao;
	private TweetNotificationBLI tweetNotificationBli;
	
	// TODO: need to figure out how to use guice to inject dependency
	public NotificationServlet() {
		this.emailService = new EmailServiceImpl();
		this.neighborhoodDao = new NeighborhoodDAOImpl();
		this.accountDao = new AccountDAOImpl(neighborhoodDao);
		this.tweetDao = new TweetDAOImpl(null /* passing null for HashtagDao (hack) */);
		this.accountNotificationDao = new AccountNotificationDAOImpl();
		this.tweetNotificationBli = new TweetNotificationBLIImpl(accountDao, neighborhoodDao, tweetDao, accountNotificationDao, emailService);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String actionString = req.getParameter(ZipllyServerConstants.ACTION_KEY);
		EmailAction action = EmailAction.valueOf(actionString);
		String senderAccountId = req.getParameter(ZipllyServerConstants.SENDER_ACCOUNT_ID_KEY);
		String emailTemplateName = req.getParameter(ZipllyServerConstants.EMAIL_TEMPLATE_ID_KEY);
		EmailTemplate emailTemplate = EmailTemplate.valueOf(emailTemplateName);

		logger.log(Level.INFO, String.format("Received notification request with action %s, sender %s, email template %s",
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
			tweetNotificationBli.sendEmail(recipientEmail, recipientName, senderEmail, senderName, emailTemplate);
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
