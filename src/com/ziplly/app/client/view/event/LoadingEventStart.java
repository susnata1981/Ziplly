package com.ziplly.app.client.view.event;

import com.google.gwt.event.shared.GwtEvent;
import com.ziplly.app.client.view.handler.LoadingEventStartHandler;

public class LoadingEventStart extends GwtEvent<LoadingEventStartHandler> {
	public static Type<LoadingEventStartHandler> TYPE = new Type<LoadingEventStartHandler>();
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<LoadingEventStartHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LoadingEventStartHandler handler) {
		handler.onEvent(this);
	}
}
