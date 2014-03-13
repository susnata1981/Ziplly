package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

import com.ziplly.app.model.AccountType;
import com.ziplly.app.model.BusinessType;

public class CreateRegistrationAction implements Action<CreateRegistrationResult> {
	private AccountType type;
	private String email;
	private BusinessType businessType;

	public CreateRegistrationAction() {
	}

	public CreateRegistrationAction(String email, AccountType type, BusinessType btype) {
		this.setEmail(email);
		this.setType(type);
		this.setBusinessType(btype);
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

	public BusinessType getBusinessType() {
		return businessType;
	}

	public void setBusinessType(BusinessType businessType) {
		this.businessType = businessType;
	}
}
