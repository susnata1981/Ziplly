package com.ziplly.app.client.view.handler;

import com.google.gwt.event.shared.EventHandler;
import com.ziplly.app.client.view.event.LoadingEventStart;

public interface LoadingEventStartHandler extends EventHandler {
	void onEvent(LoadingEventStart event);
}
