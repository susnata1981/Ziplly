package com.ziplly.app.shared;

import com.ziplly.app.model.AccountType;
import com.ziplly.app.model.BusinessType;

import net.customware.gwt.dispatch.shared.Result;

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
