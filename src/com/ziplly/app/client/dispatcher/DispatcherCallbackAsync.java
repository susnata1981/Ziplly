package com.ziplly.app.client.dispatcher;

import java.util.logging.Logger;

import net.customware.gwt.dispatch.shared.Result;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.ziplly.app.client.ZGinInjector;
import com.ziplly.app.client.exceptions.GlobalErrorHandler;
import com.ziplly.app.client.exceptions.NeedsLoginException;
import com.ziplly.app.client.places.LoginPlace;

public abstract class DispatcherCallbackAsync<T extends Result> implements AsyncCallback<T> {
	private PlaceController placeController;
	private GlobalErrorHandler errorHandler;
	ZGinInjector injector = GWT.create(ZGinInjector.class);
	Logger logger = Logger.getLogger(DispatcherCallbackAsync.class.getName());

	public DispatcherCallbackAsync() {
		this.placeController = injector.getPlaceController();
		this.errorHandler = injector.getErrorHandler();
	}

	public DispatcherCallbackAsync(EventBus eventBus) {
    this.placeController = new PlaceController(eventBus);
    this.errorHandler = new GlobalErrorHandler(eventBus);
  }
	
	@Override
	public void onFailure(Throwable th) {
		errorHandler.handlerError(th);

		postHandle(th);
		if (th instanceof NeedsLoginException) {
			placeController.goTo(new LoginPlace());
		}
	}

	/**
	 * Post handle hook
	 * @param th 
	 */
	public void postHandle(Throwable th) {
		
	}
	
	protected void onAccessError() {
  }
	
	protected void onNotFoundException() {
	}
}
