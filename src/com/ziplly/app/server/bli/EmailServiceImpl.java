package com.ziplly.app.server.bli;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.server.ZipllyServerConstants;
import com.ziplly.app.shared.EmailTemplate;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class EmailServiceImpl implements EmailService {
	private static String EMAIL_TEMPLATE_DIR = "/WEB-INF/templates/email/";
	private static String APP_ADMING_EMAIL_PROP = "app.admin.email";
	private Logger logger = Logger.getLogger(EmailServiceImpl.class.getCanonicalName());

	@Inject
	Provider<ServletContext> context;

	public EmailServiceImpl() {
	}

	private void sendEmail(String recipientName,
	    String recipientEmail,
	    String senderEmail,
	    String senderName,
	    EmailTemplate template) {

		Map<String, String> data = Maps.newHashMap();
		data.put(ZipllyServerConstants.RECIPIENT_NAME_KEY, recipientName);
		data.put(ZipllyServerConstants.RECIPIENT_EMAIL_KEY, recipientEmail);
		data.put(ZipllyServerConstants.SENDER_NAME_KEY, senderName);
		data.put(ZipllyServerConstants.SENDER_EMAIL_KEY, senderEmail);
		String emailMessage = prepareEmail(data, template);
		sendEmailToFrom(
		    recipientName,
		    recipientEmail,
		    senderName,
		    senderEmail,
		    template.getSubject(),
		    emailMessage);
	}

	@Override
	public void sendTemplatedEmail(EmailEntity from,
	    EmailEntity to,
	    EmailTemplate template,
	    Map<String, String> data) {
		String emailMessage = prepareEmail(data, template);
		sendEmailToFrom(
		    data.get(ZipllyServerConstants.RECIPIENT_NAME_KEY),
		    data.get(ZipllyServerConstants.RECIPIENT_EMAIL_KEY),
		    data.get(ZipllyServerConstants.SENDER_NAME_KEY),
		    data.get(ZipllyServerConstants.SENDER_EMAIL_KEY),
		    template.getSubject(),
		    emailMessage);
	}

	private String prepareEmail(Map<String, String> data, EmailTemplate template) {
		Configuration cfg = new Configuration();
		cfg.setDefaultEncoding("UTF-8");
		cfg.setLocale(Locale.US);
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

		try {
			Template emailTemplate = cfg.getTemplate(EMAIL_TEMPLATE_DIR + template.getFilename());
			StringWriter writer = new StringWriter();
			emailTemplate.process(data, writer);
			return writer.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException te) {
			te.printStackTrace();
		}
		return null;
	}

	/**
	 * Main method to send emails. Take in 1. Recipient name 2. Recipient email 3.
	 * Subject 4. Content
	 */
	private void sendEmailToFrom(
	    String recipientName,
	    String recipientEmail,
	    String fromName,
	    String fromEmail,
	    String subject,
	    String emailContent) {

		logger.info(String.format(
		    "Sending email from: %s, to: %s, subject: %s, content: %s",
		    fromEmail,
		    recipientEmail,
		    subject,
		    emailContent));

		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props);
		Message msg = new MimeMessage(session);
		try {
			msg.setFrom(new InternetAddress(System.getProperty(
			    APP_ADMING_EMAIL_PROP,
			    ZipllyServerConstants.APP_ADMIN_NAME)));
			msg
			    .addRecipient(
			        Message.RecipientType.TO,
			        new InternetAddress(recipientEmail, recipientName));
			msg.setSubject(subject);
			msg.setContent(emailContent, "text/html; charset=utf-8");

			// Email enabled flag needs to be turned on.
			boolean emailEnabled = Boolean.valueOf(System.getProperty(StringConstants.APP_EMAIL_ENABLE));
			boolean adminEmailEnabled =
			    Boolean.valueOf(System.getProperty(ZipllyServerConstants.APP_ADMIN_EMAIL_ENABLE));
			String adminEmail = System.getProperty(ZipllyServerConstants.APP_ADMIN_EMAIL_KEY);

			if (recipientEmail.equals(adminEmail)) {
				if (adminEmailEnabled) {
					Transport.send(msg);
					return;
				}
			}

			if (emailEnabled) {
				logger.info(String.format("Sending out email to %s", recipientEmail));
				Transport.send(msg);
			}

		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Sends emails to multiple recipient.
	 */
	@Override
	public void
	    sendMultipleEmails(Account sender, List<AccountDTO> recipients, EmailTemplate template) {
		if (sender == null || recipients == null || recipients.isEmpty()) {
			throw new IllegalArgumentException("Invalid accounts passed to sendEmail");
		}
		Map<String, String> data = Maps.newHashMap();
		for (AccountDTO acct : recipients) {
			data.put(ZipllyServerConstants.RECIPIENT_EMAIL_KEY, acct.getEmail());
			data.put(ZipllyServerConstants.RECIPIENT_NAME_KEY, acct.getDisplayName());
			data.put(ZipllyServerConstants.SENDER_NAME_KEY, sender.getName());
			prepareAndSendEmail(data, template);
		}
	}

	private void prepareAndSendEmail(Map<String, String> data, EmailTemplate template) {
		Configuration cfg = new Configuration();
		cfg.setDefaultEncoding("UTF-8");
		cfg.setLocale(Locale.US);
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

		try {
			Template emailTemplate = cfg.getTemplate(EMAIL_TEMPLATE_DIR + template.getFilename());
			StringWriter writer = new StringWriter();
			emailTemplate.process(data, writer);
			sendEmailToFrom(
			    data.get(ZipllyServerConstants.RECIPIENT_NAME_KEY),
			    data.get(ZipllyServerConstants.RECIPIENT_EMAIL_KEY),
			    ZipllyServerConstants.APP_ADMIN_EMAIL_NAME,
			    System.getProperty(ZipllyServerConstants.APP_ADMIN_EMAIL_KEY),
			    template.getSubject(),
			    writer.toString());

		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException te) {
			te.printStackTrace();
		}
	}

	@Override
	public void
	    sendNonTemplatedEmail(String subject, String message, EmailEntity from, EmailEntity to) throws MessagingException {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props);

		logger.info(String.format(
		    "Sending email from: %s, to: %s, subject: %s, content: %s",
		    from.email,
		    to.email,
		    subject,
		    message));

		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(System
			    .getProperty(APP_ADMING_EMAIL_PROP, "Ziplly.com, Admin")));
			// msg.setFrom(new InternetAddress(from.email));
			msg.setRecipient(RecipientType.TO, new InternetAddress(to.email));
			msg.setSubject(subject);
			msg.setText(message);
			Transport.send(msg);
		} catch (AddressException ae) {
			throw ae;
		} catch (MessagingException me) {
			throw me;
		}
	}

	public static class EmailEntity {
		public String name;
		public String email;
	}

	@Override
	public void sendTemplatedEmailFromSender(EmailServiceImpl.Builder builder) {
		EmailServiceImpl.this.sendEmail(
		    builder.recipientName,
		    builder.recipientEmail,
		    builder.senderEmail,
		    builder.senderName,
		    builder.template);
	}

	/**
	 * You've to supply your own CONTENT.
	 */
	@Override
	public void sendEmail(EmailServiceImpl.Builder builder) throws MessagingException {
		EmailEntity from = new EmailEntity();
		from.email = builder.senderEmail;
		from.name = builder.senderName;

		EmailEntity to = new EmailEntity();
		to.email = builder.recipientEmail;
		to.name = builder.recipientName;
		EmailServiceImpl.this.sendNonTemplatedEmail(builder.subject, builder.content, from, to);
	}

	public static class Builder {
		String recipientName;
		String recipientEmail;
		String senderName;
		String senderEmail;
		String subject;
		String content;
		EmailTemplate template;

		public Builder() {
		}

		public Builder setRecipientName(String recipientName) {
			this.recipientName = recipientName;
			return this;
		}

		public Builder setRecipientEmail(String recipientEmail) {
			this.recipientEmail = recipientEmail;
			return this;
		}

		public Builder setSenderName(String senderName) {
			this.senderName = senderName;
			return this;
		}

		public Builder setSenderEmail(String email) {
			this.senderEmail = email;
			return this;
		}

		public Builder setEmailTemplate(EmailTemplate template) {
			this.template = template;
			return this;
		}

		public Builder setSubject(String subject) {
			this.subject = subject;
			return this;
		}

		public Builder setContent(String content) {
			this.content = content;
			return this;
		}

		public Builder newBuilder() {
			return new Builder();
		}
	}
}
