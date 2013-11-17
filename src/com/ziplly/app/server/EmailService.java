package com.ziplly.app.server;

import java.util.Map;

import com.ziplly.app.shared.EmailTemplate;

public interface EmailService {
	void sendEmail(String recipientName, String recipientEmail, EmailTemplate template);
	void sendEmail(Map<String, String> data, EmailTemplate template);
}
