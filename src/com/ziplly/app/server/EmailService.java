package com.ziplly.app.server;

import java.util.List;
import java.util.Map;

import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.shared.EmailTemplate;

public interface EmailService {
	void sendEmail(String recipientName, String recipientEmail, EmailTemplate template);
	void sendEmail(Map<String, String> data, EmailTemplate template);
	void sendEmail(Account sender, List<AccountDTO> recipients, EmailTemplate template);
}
