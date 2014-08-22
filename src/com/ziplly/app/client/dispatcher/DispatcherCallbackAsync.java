package com.ziplly.app.client.dispatcher;

import java.util.logging.Logger;

import net.customware.gwt.dispatch.shared.Result;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.ziplly.app.client.exceptions.GlobalErrorHandler;

public abstract class DispatcherCallbackAsync<T extends Result> implements AsyncCallback<T> {
	Logger logger = Logger.getLogger(DispatcherCallbackAsync.class.getName());

	private GlobalErrorHandler errorHandler;
	protected EventBus eventBus;
	
	public DispatcherCallbackAsync(EventBus eventBus) {
	  assert(eventBus != null);
	  this.eventBus = eventBus;
	  this.errorHandler = new GlobalErrorHandler(eventBus);
  }
	
	@Override
	public void onFailure(Throwable th) {
	  assert(errorHandler != null);
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
