package com.ziplly.app.client.dispatcher;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import net.customware.gwt.dispatch.shared.Result;

public abstract class DispatcherCallbackAsync<T extends Result> implements AsyncCallback<T> {
	@Override
	public void onFailure(Throwable caught) {
		Window.alert(caught.getLocalizedMessage());
	}
}
