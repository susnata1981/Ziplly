package com.ziplly.app.client.view.event;

import com.google.gwt.event.shared.GwtEvent;
import com.ziplly.app.client.view.handler.LoadingEventEndHandler;

public class LoadingEventEnd extends GwtEvent<LoadingEventEndHandler> {
	public static Type<LoadingEventEndHandler> TYPE = new Type<LoadingEventEndHandler>();
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<LoadingEventEndHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LoadingEventEndHandler handler) {
		handler.onEvent(this);
	}

}
