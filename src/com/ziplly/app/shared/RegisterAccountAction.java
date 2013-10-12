package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

import com.ziplly.app.model.AccountDTO;

public class RegisterAccountAction implements Action<RegisterAccountResult> {
	private AccountDTO account;

	public RegisterAccountAction() {
	}
	
	public RegisterAccountAction(AccountDTO account) {
		this.setAccount(account);
	}

	public AccountDTO getAccount() {
		return account;
	}

	public void setAccount(AccountDTO account) {
		this.account = account;
	}
}
