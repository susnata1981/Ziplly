package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class SendEmailAction implements Action<SendEmailResult> {
	private Long emailId;

	public SendEmailAction() {
	}
	
	public SendEmailAction(Long id) {
		this.setEmailId(id);
	}

	public Long getEmailId() {
		return emailId;
	}

	public void setEmailId(Long emailId) {
		this.emailId = emailId;
	}

}
