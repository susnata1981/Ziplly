package com.ziplly.app.client.exceptions;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.ErrorDefinitions.ErrorDefinition;
import com.ziplly.app.client.view.event.NeedsLoginEvent;
import com.ziplly.app.client.widget.AlertModal;

public class GlobalErrorHandler {
	AlertModal modal = new AlertModal();
	private EventBus eventBus;

	@Inject
	public GlobalErrorHandler(EventBus eventBus) {
		this.eventBus = eventBus;
  }
	
	public void handlerError(Throwable th) {
		ErrorDefinition<?> errorDef = ErrorDefinitions.getErrorDefinition(th.getClass());
		if (errorDef == null) {
			return;
		}
		
		modal.showMessage(errorDef.getErrorMessage(), errorDef.getType());
		if (errorDef.getCode() == ErrorCodes.NeedsLoginError) {
		  eventBus.fireEvent(new NeedsLoginEvent());
		}
		
		postHandle();
	}

	public void postHandle() {
  }
}
