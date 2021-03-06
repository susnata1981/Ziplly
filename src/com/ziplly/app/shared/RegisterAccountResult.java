package com.ziplly.app.shared;

import com.ziplly.app.model.AccountDTO;

import net.customware.gwt.dispatch.shared.Result;

public class RegisterAccountResult implements Result{
	private AccountDTO account;
	private Long uid;

	public RegisterAccountResult() {
	}
	
	public RegisterAccountResult(AccountDTO account) {
		this.setAccount(account);
	}

	public AccountDTO getAccount() {
		return account;
	}

	public void setAccount(AccountDTO account) {
		this.account = account;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}
	
	public Long getUid() {
		return uid;
	}
}
