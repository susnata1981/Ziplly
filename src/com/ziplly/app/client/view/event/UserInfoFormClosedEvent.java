package com.ziplly.app.client.view.event;

import com.google.gwt.event.shared.GwtEvent;
import com.ziplly.app.client.view.handler.UserInfoFormClosedEventHandler;

public class UserInfoFormClosedEvent extends GwtEvent<UserInfoFormClosedEventHandler>{

	public static Type<UserInfoFormClosedEventHandler> TYPE = new Type<UserInfoFormClosedEventHandler>();
	
	@Override
	public Type<UserInfoFormClosedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(UserInfoFormClosedEventHandler handler) {
		handler.onEvent(this);
	}

}
