package com.ziplly.app.client.view.event;

import com.google.gwt.event.shared.GwtEvent;
import com.ziplly.app.client.view.handler.AccountViewEventHandler;

public class AccountViewEvent extends GwtEvent<AccountViewEventHandler> {

	public static final Type<AccountViewEventHandler> TYPE = new Type<AccountViewEventHandler>();

	@Override
	public Type<AccountViewEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AccountViewEventHandler handler) {
		handler.onEvent(this);
	}
}
