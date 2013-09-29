package com.ziplly.app.client.view.handler;

import com.google.gwt.event.shared.EventHandler;
import com.ziplly.app.client.view.event.LogoutEvent;

public interface LogoutEventHandler extends EventHandler {
	void onEvent(LogoutEvent event);
}
