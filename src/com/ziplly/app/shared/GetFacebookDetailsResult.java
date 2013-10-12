package com.ziplly.app.shared;

import com.ziplly.app.model.AccountDTO;

import net.customware.gwt.dispatch.shared.Result;

public class GetFacebookDetailsResult implements Result{
	
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
