package com.ziplly.app.client.view.handler;

import com.google.gwt.event.shared.EventHandler;
import com.ziplly.app.client.view.event.TweetNotAvailableEvent;

public interface TweetNotAvailableEventHandler extends EventHandler {
	public void onEvent(TweetNotAvailableEvent event);
}
