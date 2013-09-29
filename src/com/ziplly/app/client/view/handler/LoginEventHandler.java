package com.ziplly.app.client.view.handler;

import com.google.gwt.event.shared.EventHandler;
import com.ziplly.app.client.view.event.LoginEvent;

public interface LoginEventHandler extends EventHandler {
	void onEvent(LoginEvent event);
}
