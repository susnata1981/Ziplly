package com.ziplly.app.client;

import com.ziplly.app.model.AccountDTO;

public class ApplicationContext {
	private AccountDTO account;

	public ApplicationContext() {
	}
	
	public AccountDTO getAccount() {
		return account;
	}

	public void setAccount(AccountDTO account) {
		this.account = account;
	}
}
