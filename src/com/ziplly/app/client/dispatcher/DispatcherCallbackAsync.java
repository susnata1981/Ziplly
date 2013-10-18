package com.ziplly.app.client.dispatcher;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.NeedsLoginException;
import com.ziplly.app.client.places.AccountPlace;

import net.customware.gwt.dispatch.shared.Result;

public abstract class DispatcherCallbackAsync<T extends Result> implements AsyncCallback<T> {
	@Inject
	private PlaceController placeController;

	public DispatcherCallbackAsync() {
	}
	
	@Override
	public void onFailure(Throwable caught) {
		if (caught instanceof NeedsLoginException) {
			placeController.goTo(new AccountPlace());
		}
		Window.alert(caught.getLocalizedMessage());
	}
}
