package com.ziplly.app.server;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ziplly.app.server.EmailServiceImpl.EmailEntity;

public class MailHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private EmailService emailService;

	public MailHandlerServlet() {
		emailService = new EmailServiceImpl();
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		try {
			MimeMessage message = new MimeMessage(session, req.getInputStream());
			Address[] from = message.getFrom();
			String subject = message.getSubject();
			String content = "";
			content = getText(message);
			String adminEmail = System.getProperty(ZipllyServerConstants.APP_ADMIN_EMAIL_KEY);
			EmailEntity sender = new EmailEntity();
			sender.email = from[0].toString();
			EmailEntity recipient = new EmailEntity();
			recipient.email = adminEmail;

			emailService.sendNonTemplatedEmail(subject, content, sender, recipient);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	private String getText(Part p) throws MessagingException, IOException {
		if (p.isMimeType("text/*")) {
			String s = (String) p.getContent();
			return s;
		}

		if (p.isMimeType("multipart/alternative")) {
			// prefer html text over plain text
			Multipart mp = (Multipart) p.getContent();
			String text = null;
			for (int i = 0; i < mp.getCount(); i++) {
				Part bp = mp.getBodyPart(i);
				if (bp.isMimeType("text/plain")) {
					if (text == null)
						text = getText(bp);
					continue;
				} else if (bp.isMimeType("text/html")) {
					String s = getText(bp);
					if (s != null)
						return s;
				} else {
					return getText(bp);
				}
			}
			return text;
		} else if (p.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) p.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				String s = getText(mp.getBodyPart(i));
				if (s != null)
					return s;
			}
		}
		return null;
	}
}