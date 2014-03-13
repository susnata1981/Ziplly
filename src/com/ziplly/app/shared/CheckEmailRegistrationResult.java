package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.AccountType;
import com.ziplly.app.model.BusinessType;

public class CheckEmailRegistrationResult implements Result {
	private AccountType accountType;
	private BusinessType businessType;

	public CheckEmailRegistrationResult() {
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public BusinessType getBusinessType() {
		return businessType;
	}

	public void setBusinessType(BusinessType businessType) {
		this.businessType = businessType;
	}
}
