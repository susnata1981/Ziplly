package com.ziplly.app.client.view.handler;

import com.google.gwt.event.shared.EventHandler;
import com.ziplly.app.client.view.event.UserInfoFormClosedEvent;

public interface UserInfoFormClosedEventHandler extends EventHandler {
	public void onEvent(UserInfoFormClosedEvent uifce);
}
