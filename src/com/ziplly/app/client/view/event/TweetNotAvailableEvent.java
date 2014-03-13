package com.ziplly.app.client.view.event;

import com.google.gwt.event.shared.GwtEvent;
import com.ziplly.app.client.view.handler.TweetNotAvailableEventHandler;

public class TweetNotAvailableEvent extends GwtEvent<TweetNotAvailableEventHandler> {
	public static Type<TweetNotAvailableEventHandler> TYPE =
	    new Type<TweetNotAvailableEventHandler>();

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<TweetNotAvailableEventHandler>
	    getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TweetNotAvailableEventHandler handler) {
		handler.onEvent(this);
	}
}
