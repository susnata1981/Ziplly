package com.ziplly.app.shared;

import com.ziplly.app.model.AccountDTO;

import net.customware.gwt.dispatch.shared.Result;

public class SwitchLocationResult implements Result {

	private AccountDTO account;

	public void setAccount(AccountDTO account) {
		this.account = account;
	}
	
	public AccountDTO getAccount() {
		return account;
	}
}
