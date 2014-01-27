package com.ziplly.app.client.view.handler;

import com.google.gwt.event.shared.EventHandler;
import com.ziplly.app.client.view.event.LoadingEventEnd;

public interface LoadingEventEndHandler extends EventHandler {
	void onEvent(LoadingEventEnd event);
}
