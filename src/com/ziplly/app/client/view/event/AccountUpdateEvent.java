package com.ziplly.app.client.view.event;

import com.google.gwt.event.shared.GwtEvent;
import com.ziplly.app.client.view.handler.AccountUpdateEventHandler;
import com.ziplly.app.model.AccountDetails;

public class AccountUpdateEvent extends
		GwtEvent<AccountUpdateEventHandler> {
	
	private AccountDetails ad;
	public static Type<AccountUpdateEventHandler> TYPE = new Type<AccountUpdateEventHandler>();

	public AccountUpdateEvent(AccountDetails ad) {
		this.ad = ad;
	}
	
	@Override
	public Type<AccountUpdateEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AccountUpdateEventHandler handler) {
		handler.onEvent(this);
	}

	public AccountDetails getAccountDetails() {
		return ad;
	}
}