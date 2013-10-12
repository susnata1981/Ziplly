package com.ziplly.app.shared;

import com.ziplly.app.model.AccountDTO;

import net.customware.gwt.dispatch.shared.Result;

public class GetLoggedInUserResult implements Result {
	private AccountDTO account;

	public GetLoggedInUserResult(AccountDTO account) {
		this.setAccount(account);
	}

	public GetLoggedInUserResult() {
	}

	public AccountDTO getAccount() {
		return account;
	}

	public void setAccount(AccountDTO account) {
		this.account = account;
	}
}
