package com.ziplly.app.server;

import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.server.EmailServiceImpl.EmailEntity;
import com.ziplly.app.shared.EmailTemplate;

public interface EmailService {
	void sendEmail(String recipientName, String recipientEmail, EmailTemplate template);
	void sendEmail(Map<String, String> data, EmailTemplate template);
	void sendEmail(Account sender, List<AccountDTO> recipients, EmailTemplate template);
//	void sendEmail(String subject, String message, String from, String to) throws MessagingException;
	void sendEmail(String subject, String message, EmailEntity from, EmailEntity to)
			throws MessagingException;
}
