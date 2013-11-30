package com.ziplly.app.shared;

import com.ziplly.app.model.AccountType;

import net.customware.gwt.dispatch.shared.Action;

public class CreateRegistrationAction implements Action<CreateRegistrationResult>{
	private AccountType type;
	private String email;

	public CreateRegistrationAction() {
	}
	
	public CreateRegistrationAction(String email, AccountType type) {
		this.setEmail(email);
		this.setType(type);
	}

	public AccountType getType() {
		return type;
	}

	public void setType(AccountType type) {
		this.type = type;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
