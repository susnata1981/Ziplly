package com.ziplly.app.server.bli;

import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.server.bli.EmailServiceImpl.Builder;
import com.ziplly.app.server.bli.EmailServiceImpl.EmailEntity;
import com.ziplly.app.server.model.jpa.Account;
import com.ziplly.app.shared.EmailTemplate;

public interface EmailService {
	// void sendEmail(String recipientName, String recipientEmail, EmailTemplate
	// template);
	// void sendEmail(Map<String, String> data, EmailTemplate template);
	void sendMultipleEmails(Account sender, List<AccountDTO> recipients, EmailTemplate template);

	// void sendEmail(String subject, String message, String from, String to)
	// throws MessagingException;
	void
	    sendNonTemplatedEmail(String subject, String message, EmailEntity from, EmailEntity to) throws MessagingException;

	void sendEmail(Builder builder) throws MessagingException;

	void sendTemplatedEmailFromSender(Builder builder);

	void sendTemplatedEmail(EmailEntity from,
	    EmailEntity to,
	    EmailTemplate template,
	    Map<String, String> data);
}
