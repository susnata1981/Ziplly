package com.ziplly.app.client.dispatcher;

import net.customware.gwt.dispatch.shared.Result;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.ziplly.app.client.exceptions.NeedsLoginException;
import com.ziplly.app.client.places.LoginPlace;

public abstract class DispatcherCallbackAsync<T extends Result> implements AsyncCallback<T> {
	private PlaceController placeController;

	public DispatcherCallbackAsync() {
	}
	
	@Override
	public void onFailure(Throwable caught) {
		if (caught instanceof NeedsLoginException) {
			getPlaceController().goTo(new LoginPlace());
			return;
		}
		Window.alert(caught.getLocalizedMessage());
	}

	public PlaceController getPlaceController() {
		return placeController;
	}

	public void setPlaceController(PlaceController placeController) {
		this.placeController = placeController;
	}
}
