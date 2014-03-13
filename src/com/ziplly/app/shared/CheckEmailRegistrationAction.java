package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class CheckEmailRegistrationAction implements Action<CheckEmailRegistrationResult> {
	private String email;
	private long code;

	public CheckEmailRegistrationAction() {
	}

	public CheckEmailRegistrationAction(String email, long code) {
		this.setEmail(email);
		this.setCode(code);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public long getCode() {
		return code;
	}

	public void setCode(long code) {
		this.code = code;
	}
}
