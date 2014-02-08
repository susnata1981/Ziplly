package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.AccountDTO;

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
