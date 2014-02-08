package com.ziplly.app.server;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

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
import com.ziplly.app.shared.EmailTemplate;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class EmailServiceImpl implements EmailService {
	private static String EMAIL_TEMPLATE_DIR = "/WEB-INF/templates/email/";
	private static String APP_ADMING_EMAIL_PROP = "app.admin.email";
	
	@Inject
	Provider<ServletContext> context;
	
	public EmailServiceImpl() {
	}
	
	@Override
	public void sendEmail(String recipientName, String recipientEmail, EmailTemplate template) {
		Map<String, String> data = Maps.newHashMap();
		data.put("recipientName", recipientName);
		data.put("recipientEmail", recipientEmail);
		String emailMessage = prepareEmail(data, template);
		sendEmail(recipientName, recipientEmail, template.getSubject(), emailMessage);
	}

	/**
	 * Main method to send emails. Take in 
	 * 1. Recipient name
	 * 2. Recipient email
	 * 3. Subject
	 * 4. Content
	 */
	private void sendEmail(String recipientName, String recipientEmail, String subject, String emailContent) {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props);
		Message msg = new MimeMessage(session);
		try {
			msg.setFrom(new InternetAddress(System.getProperty(APP_ADMING_EMAIL_PROP, "Ziplly.com, Admin")));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail, recipientName));
			msg.setSubject(subject);
			msg.setContent(emailContent, "text/html; charset=utf-8");
			
//			System.out.println("Sending email from:"+System.getProperty(APP_ADMING_EMAIL_PROP)+" to:"+recipientEmail);
			
			// Email enabled flag needs to be turned on.
			boolean emailEnabled = Boolean.valueOf(System.getProperty(StringConstants.APP_EMAIL_ENABLE));
			boolean adminEmailEnabled = Boolean.valueOf(System.getProperty(StringConstants.APP_ADMIN_EMAIL_ENABLE));
			String adminEmail = System.getProperty(StringConstants.APP_ADMIN_EMAIL_KEY);
			
			if (recipientEmail.equals(adminEmail)) {
				if (adminEmailEnabled) {
					Transport.send(msg);
					return;
				}
			}

			if (emailEnabled) {
				Transport.send(msg);
			}
			
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void sendEmail(Map<String, String> data, EmailTemplate template) {
		if (data == null || !data.containsKey(StringConstants.RECIPIENT_EMAIL)
				|| !data.containsKey(StringConstants.RECIPIENT_NAME_KEY)) {
			throw new IllegalArgumentException("Recipient name of email not present.");
		}
		
		prepareAndSendEmail(data, template);
//		sendEmail(data.get(StringConstants.RECIPIENT_NAME_KEY), data.get(StringConstants.RECIPIENT_EMAIL), emailMessage);
	}

	/**
	 * Sends emails to multiple recipient.
	 */
	@Override
	public void sendEmail(Account sender, List<AccountDTO> recipients, EmailTemplate template) {
		if (sender == null || recipients == null || recipients.isEmpty()) {
			throw new IllegalArgumentException("Invalid accounts passed to sendEmail");
		}
		Map<String, String> data = Maps.newHashMap();
		for(AccountDTO acct : recipients) {
			data.put(StringConstants.RECIPIENT_EMAIL, acct.getEmail());
			data.put(StringConstants.RECIPIENT_NAME_KEY, acct.getDisplayName());
			data.put(StringConstants.SENDER_NAME_KEY, sender.getName());
			prepareAndSendEmail(data, template);
		}
	}

	private String prepareEmail(Map<String, String> data, EmailTemplate template) {
		Configuration cfg = new Configuration();
		cfg.setDefaultEncoding("UTF-8");
		cfg.setLocale(Locale.US);
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		
		try {
			Template emailTemplate = cfg.getTemplate(EMAIL_TEMPLATE_DIR+template.getFilename());
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
	
	private void prepareAndSendEmail(Map<String, String> data, EmailTemplate template) {
		Configuration cfg = new Configuration();
		cfg.setDefaultEncoding("UTF-8");
		cfg.setLocale(Locale.US);
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		
		try {
			Template emailTemplate = cfg.getTemplate(EMAIL_TEMPLATE_DIR + template.getFilename());
			StringWriter writer = new StringWriter();
			emailTemplate.process(data, writer);
			sendEmail(data.get(StringConstants.RECIPIENT_NAME_KEY),
					data.get(StringConstants.RECIPIENT_EMAIL) , 
					template.getSubject(),
					writer.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException te) {
			te.printStackTrace();
		}
	}
	
	@Override
	public void sendEmail(String subject, String message, EmailEntity from, EmailEntity to) throws MessagingException {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props);
		
		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(from.email));
			msg.setRecipient(RecipientType.TO, new InternetAddress(to.email));
			msg.setSubject(subject);
			msg.setText(message);
			Transport.send(msg);
		} catch(AddressException ae) {
			throw ae;
		} catch(MessagingException me) {
			throw me;
		}
	}
	
	public static class EmailEntity {
		public String name;
		public String email;
	}
}
