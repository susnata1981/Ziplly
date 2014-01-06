package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class EmailAdminAction implements Action<EmailAdminResult> {
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	private String subject;
	private String content;
	private String from;

	public EmailAdminAction() {
	}
	
	public EmailAdminAction(String from, String content, String subject) {
		this.from = from;
		this.content = content;
		this.subject = subject;
	}
}
