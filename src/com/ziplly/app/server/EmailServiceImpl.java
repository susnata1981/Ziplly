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
		sendEmail(recipientName, recipientEmail, emailMessage);
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
			Template emailTemplate = cfg.getTemplate(EMAIL_TEMPLATE_DIR+template.getFilename());
			StringWriter writer = new StringWriter();
			emailTemplate.process(data, writer);
			sendEmail(data.get(StringConstants.RECIPIENT_NAME_KEY),
					data.get(StringConstants.RECIPIENT_EMAIL) , 
					writer.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException te) {
			te.printStackTrace();
		}
	}
	
	// TODO (can recipientName be null)
	private void sendEmail(String recipientName, String recipientEmail, String emailContent) {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props);
		Message msg = new MimeMessage(session);
		try {
			msg.setFrom(new InternetAddress(System.getProperty(APP_ADMING_EMAIL_PROP, "Ziplly.com, Admin")));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail, recipientName));
			msg.setSubject("Welcome to ziplly");
			msg.setText(emailContent);
			System.out.println("Sending email from:"+System.getProperty(APP_ADMING_EMAIL_PROP)+" to:"+recipientEmail);
			Transport.send(msg);
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

	@Override
	public void sendEmail(String subject, String message, String from, String to) throws MessagingException {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props);
		
		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(from));
			msg.setRecipient(RecipientType.TO, new InternetAddress(to));
			msg.setSubject(subject);
			msg.setText(message);
			Transport.send(msg);
		} catch(AddressException ae) {
			throw ae;
		} catch(MessagingException me) {
			throw me;
		}
	}
}
