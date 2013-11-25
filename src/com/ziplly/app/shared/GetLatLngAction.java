package com.ziplly.app.shared;

import com.ziplly.app.model.AccountDTO;

import net.customware.gwt.dispatch.shared.Action;

public class GetLatLngAction implements Action<GetLatLngResult> {
	private AccountDTO account;
	
	public GetLatLngAction() {
	}
	
	public AccountDTO getAccount() {
		return account;
	}

	public void setAccount(AccountDTO account) {
		this.account = account;
	}
}
	