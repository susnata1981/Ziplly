package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class GetPublicAccountDetailsAction implements Action<GetAccountDetailsResult>{
	private Long accountId;

	public GetPublicAccountDetailsAction() {
	}
	
	public GetPublicAccountDetailsAction(Long accountId) {
		this.setAccountId(accountId);
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
}
