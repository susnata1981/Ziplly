package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.AccountDTO;

public class VerifyPasswordRecoveryHashResult implements Result {
	private AccountDTO account;

	public VerifyPasswordRecoveryHashResult() {
	}

	public VerifyPasswordRecoveryHashResult(AccountDTO account) {
		this.setAccount(account);
	}

	public AccountDTO getAccount() {
		return account;
	}

	public void setAccount(AccountDTO account) {
		this.account = account;
	}
}
