package com.ziplly.app.client.dispatcher;

import net.customware.gwt.dispatch.shared.Result;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public abstract class DispatcherCallbackAsync<T extends Result> implements AsyncCallback<T> {
	private PlaceController placeController;

	public DispatcherCallbackAsync() {
	}
	
	// TODO 
	@Override
	public void onFailure(Throwable caught) {
//		if (caught instanceof NeedsLoginException) {
//			getPlaceController().goTo(new LoginPlace());
//			return;
//		}
		Window.alert(caught.getLocalizedMessage());
	}

	public PlaceController getPlaceController() {
		return placeController;
	}

	@Inject
	public void setPlaceController(PlaceController placeController) {
		this.placeController = placeController;
	}
}
