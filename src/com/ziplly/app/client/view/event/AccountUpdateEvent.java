package com.ziplly.app.client.view.event;

import com.google.gwt.event.shared.GwtEvent;
import com.ziplly.app.client.view.handler.AccountUpdateEventHandler;
import com.ziplly.app.model.AccountDTO;

public class AccountUpdateEvent extends GwtEvent<AccountUpdateEventHandler> {
	public static Type<AccountUpdateEventHandler> TYPE = new Type<AccountUpdateEventHandler>();
	private AccountDTO account;

	public AccountUpdateEvent(AccountDTO account) {
		this.setAccount(account);
	}

	@Override
	public Type<AccountUpdateEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AccountUpdateEventHandler handler) {
		handler.onEvent(this);
	}

	public AccountDTO getAccount() {
		return account;
	}

	public void setAccount(AccountDTO account) {
		this.account = account;
	}
}