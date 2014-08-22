package com.ziplly.app.client.activities;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class DefaultViewLoaderAsyncCallback<T> implements AsyncCallback<T> {

	@Override
	public void onFailure(Throwable caught) {
		Window.alert("Sorry, lost network connection, please try again");
	}

	@Override
	public abstract void onSuccess(T result);

}
