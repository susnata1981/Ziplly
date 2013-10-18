package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class GetAccountByIdAction implements Action<GetAccountByIdResult> {
	private Long accountId;

	public GetAccountByIdAction() {
	}

	public GetAccountByIdAction(Long id) {
		this.setAccountId(id);
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
}
