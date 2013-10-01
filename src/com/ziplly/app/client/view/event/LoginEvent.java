package com.ziplly.app.client.view.event;

import com.google.gwt.event.shared.GwtEvent;
import com.ziplly.app.client.view.handler.LoginEventHandler;
import com.ziplly.app.model.AccountDTO;

public class LoginEvent extends GwtEvent<LoginEventHandler> {
	public static Type<LoginEventHandler> TYPE = new Type<LoginEventHandler>();
	private AccountDTO account;
	
	public LoginEvent(AccountDTO account) {
		this.account = account;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<LoginEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LoginEventHandler handler) {
		handler.onEvent(this);
	}

	public AccountDTO getAccount() {
		return account;
	}
}
