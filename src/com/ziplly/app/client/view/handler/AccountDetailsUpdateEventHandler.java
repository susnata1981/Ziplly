package com.ziplly.app.client.view.handler;

import com.google.gwt.event.shared.EventHandler;
import com.ziplly.app.client.view.event.AccountDetailsUpdateEvent;

public interface AccountDetailsUpdateEventHandler extends EventHandler {
	public void onEvent(AccountDetailsUpdateEvent event);
}
