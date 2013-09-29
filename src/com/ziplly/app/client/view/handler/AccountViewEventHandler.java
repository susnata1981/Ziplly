package com.ziplly.app.client.view.handler;

import com.google.gwt.event.shared.EventHandler;
import com.ziplly.app.client.view.event.AccountViewEvent;


public interface AccountViewEventHandler extends EventHandler {
	public void onEvent(AccountViewEvent event);
}
