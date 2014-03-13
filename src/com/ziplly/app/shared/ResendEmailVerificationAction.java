package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class ResendEmailVerificationAction implements Action<ResendEmailVerificationResult> {
	private String email;

	public ResendEmailVerificationAction() {
	}

	public ResendEmailVerificationAction(String email) {
		this.setEmail(email);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
