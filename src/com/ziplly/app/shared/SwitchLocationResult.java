package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.AccountDTO;

public class SwitchLocationResult implements Result {

	private AccountDTO account;

	public void setAccount(AccountDTO account) {
		this.account = account;
	}

	public AccountDTO getAccount() {
		return account;
	}
}
