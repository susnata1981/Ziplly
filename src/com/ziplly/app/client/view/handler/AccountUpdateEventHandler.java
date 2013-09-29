package com.ziplly.app.client.view.handler;

import com.google.gwt.event.shared.EventHandler;
import com.ziplly.app.client.view.event.AccountUpdateEvent;

public interface AccountUpdateEventHandler extends EventHandler {
	void onEvent(AccountUpdateEvent event);
}
