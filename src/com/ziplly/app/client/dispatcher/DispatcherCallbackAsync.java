package com.ziplly.app.client.dispatcher;

import java.util.logging.Logger;

import net.customware.gwt.dispatch.shared.Result;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.ziplly.app.client.ZGinInjector;
import com.ziplly.app.client.exceptions.GlobalErrorHandler;

public abstract class DispatcherCallbackAsync<T extends Result> implements AsyncCallback<T> {
	private GlobalErrorHandler errorHandler;
	ZGinInjector injector = GWT.create(ZGinInjector.class);
	Logger logger = Logger.getLogger(DispatcherCallbackAsync.class.getName());

	public DispatcherCallbackAsync() {
		this.errorHandler = new GlobalErrorHandler();
	}

	public DispatcherCallbackAsync(EventBus eventBus) {
	  this.errorHandler = new GlobalErrorHandler();
  }
	
	@Override
	public void onFailure(Throwable th) {
		errorHandler.handlerError(th);

		postHandle(th);
	}

	/**
	 * Post handle hook
	 */
	public void postHandle(Throwable th) {
	}
	
	@Deprecated
  protected void onAccessError() {
  }
}
