package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class SendPasswordRecoveryEmailAction implements Action<SendPasswordRecoveryEmailResult>{
	private String email;

	public SendPasswordRecoveryEmailAction() {
	}
	
	public SendPasswordRecoveryEmailAction(String email) {
		this.setEmail(email);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
