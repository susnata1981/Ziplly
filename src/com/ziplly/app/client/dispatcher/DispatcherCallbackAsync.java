package com.ziplly.app.client.dispatcher;

import net.customware.gwt.dispatch.shared.Result;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.ziplly.app.client.ZGinInjector;
import com.ziplly.app.client.exceptions.NeedsLoginException;
import com.ziplly.app.client.places.LoginPlace;

public abstract class DispatcherCallbackAsync<T extends Result> implements AsyncCallback<T> {
	private PlaceController placeController;
	ZGinInjector injector = GWT.create(ZGinInjector.class);
	
	public DispatcherCallbackAsync() {
		this.placeController = injector.getPlaceController();
	}
	
	@Override
	public void onFailure(Throwable caught) {
		if (caught instanceof NeedsLoginException) {
			placeController.goTo(new LoginPlace());
			return;
		}
		Window.alert(caught.getLocalizedMessage());
	}
}
