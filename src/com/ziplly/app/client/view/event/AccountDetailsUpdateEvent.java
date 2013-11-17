package com.ziplly.app.client.view.event;

import com.google.gwt.event.shared.GwtEvent;
import com.ziplly.app.client.view.handler.AccountDetailsUpdateEventHandler;

public class AccountDetailsUpdateEvent extends GwtEvent<AccountDetailsUpdateEventHandler>{

	public static final Type<AccountDetailsUpdateEventHandler> TYPE = new Type<AccountDetailsUpdateEventHandler>();

	@Override
	public Type<AccountDetailsUpdateEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AccountDetailsUpdateEventHandler handler) {
		handler.onEvent(this);
	}

}
