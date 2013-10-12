package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class ValidateLoginAction implements Action<ValidateLoginResult>{
	private String password;
	private String email;
	
	public ValidateLoginAction() {
	}
	
	public ValidateLoginAction(String email, String password) {
		this.setEmail(email);
		this.setPassword(password);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
