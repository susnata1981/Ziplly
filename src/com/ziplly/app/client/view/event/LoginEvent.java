package com.ziplly.app.client.view.event;

import com.google.gwt.event.shared.GwtEvent;
import com.ziplly.app.client.view.handler.LoginEventHandler;
import com.ziplly.app.model.AccountDetails;

public class LoginEvent extends GwtEvent<LoginEventHandler>{
	
	public static Type<LoginEventHandler> TYPE = new Type<LoginEventHandler>();
	private AccountDetails ad;
	
	public LoginEvent(AccountDetails ad) {
		this.ad = ad;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<LoginEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LoginEventHandler handler) {
		handler.onEvent(this);
	}

	public AccountDetails getAccountDetails() {
		return ad;
	}
}
