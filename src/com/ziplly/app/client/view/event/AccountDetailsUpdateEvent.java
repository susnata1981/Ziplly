package com.ziplly.app.client.view.event;

import com.google.gwt.event.shared.GwtEvent;
import com.ziplly.app.client.view.handler.AccountDetailsUpdateEventHandler;
import com.ziplly.app.shared.GetAccountDetailsResult;

public class AccountDetailsUpdateEvent extends GwtEvent<AccountDetailsUpdateEventHandler> {

	public static final Type<AccountDetailsUpdateEventHandler> TYPE =
	    new Type<AccountDetailsUpdateEventHandler>();
	private GetAccountDetailsResult accountDetails;

	public AccountDetailsUpdateEvent(GetAccountDetailsResult result) {
		this.setAccountDetails(result);
	}

	@Override
	public Type<AccountDetailsUpdateEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AccountDetailsUpdateEventHandler handler) {
		handler.onEvent(this);
	}

	public GetAccountDetailsResult getAccountDetails() {
		return accountDetails;
	}

	public void setAccountDetails(GetAccountDetailsResult accountDetails) {
		this.accountDetails = accountDetails;
	}
}
