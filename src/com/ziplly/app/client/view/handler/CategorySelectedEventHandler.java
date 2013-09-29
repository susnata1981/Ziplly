package com.ziplly.app.client.view.handler;

import com.google.gwt.event.shared.EventHandler;
import com.ziplly.app.client.view.event.CategorySelectedEvent;

public interface CategorySelectedEventHandler extends EventHandler {
	public void onEvent(CategorySelectedEvent event);
}
