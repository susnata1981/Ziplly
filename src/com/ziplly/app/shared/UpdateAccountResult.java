package com.ziplly.app.shared;

import com.ziplly.app.model.AccountDTO;

import net.customware.gwt.dispatch.shared.Result;

public class UpdateAccountResult implements Result{
	private AccountDTO account;

	public UpdateAccountResult() {
	}
	
	public UpdateAccountResult(AccountDTO account) {
		this.setAccount(account);
	}

	public AccountDTO getAccount() {
		return account;
	}

	public void setAccount(AccountDTO account) {
		this.account = account;
	}
}
