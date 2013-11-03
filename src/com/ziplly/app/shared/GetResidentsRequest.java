package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

import com.ziplly.app.model.AccountDTO;

public class GetResidentsRequest implements Action<GetResidentsResult> {
	private AccountDTO account;

	public GetResidentsRequest() {
	}
	
	public GetResidentsRequest(AccountDTO account) {
		this.setAccount(account);
	}

	public AccountDTO getAccount() {
		return account;
	}

	public void setAccount(AccountDTO account) {
		this.account = account;
	}
}