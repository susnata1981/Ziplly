package com.ziplly.app.server;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.view.StringConstants;
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
		String emailMessage = prepareEmail(data, template);
		sendEmail(data.get(StringConstants.RECIPIENT_NAME), data.get(StringConstants.RECIPIENT_EMAIL), emailMessage);
	}

}
