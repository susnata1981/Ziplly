package com.ziplly.app.client.view.handler;

import com.google.gwt.event.shared.EventHandler;
import com.ziplly.app.client.view.event.AccountNotificationEvent;

public interface AccountNotificationEventHandler extends EventHandler {
	public void onEvent(AccountNotificationEvent event);
}
