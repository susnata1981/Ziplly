package com.ziplly.app.shared;

import com.ziplly.app.model.AccountDTO;

import net.customware.gwt.dispatch.shared.Action;

public class UpdateAccountAction implements Action<UpdateAccountResult>{
	private AccountDTO account;

	public UpdateAccountAction() {
	}
	
	public UpdateAccountAction(AccountDTO account) {
		this.setAccount(account);
	}

	public AccountDTO getAccount() {
		return account;
	}

	public void setAccount(AccountDTO account) {
		this.account = account;
	}
}
