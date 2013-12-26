package com.ziplly.app.client.view.event;

import java.util.List;

import com.google.gwt.event.shared.GwtEvent;
import com.ziplly.app.client.view.handler.AccountNotificationEventHandler;
import com.ziplly.app.model.AccountNotificationDTO;

public class AccountNotificationEvent extends GwtEvent<AccountNotificationEventHandler>{
	public static Type<AccountNotificationEventHandler> TYPE = new Type<AccountNotificationEventHandler>();
	private List<AccountNotificationDTO> accountNotifications;
	
	public AccountNotificationEvent(List<AccountNotificationDTO> accountNotifications) {
		this.setAccountNotifications(accountNotifications);
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<AccountNotificationEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AccountNotificationEventHandler handler) {
		handler.onEvent(this);
	}

	public List<AccountNotificationDTO> getAccountNotifications() {
		return accountNotifications;
	}

	public void setAccountNotifications(List<AccountNotificationDTO> accountNotifications) {
		this.accountNotifications = accountNotifications;
	}

}
