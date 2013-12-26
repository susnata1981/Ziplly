package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.AccountDTO;

public class GetFacebookDetailsResult implements Result {
	
	private AccountDTO account;

	public GetFacebookDetailsResult() {
	}
	
	public GetFacebookDetailsResult(AccountDTO account) {
		this.setAccount(account);
	}

	public AccountDTO getAccount() {
		return account;
	}

	public void setAccount(AccountDTO account) {
		this.account = account;
	}
}
