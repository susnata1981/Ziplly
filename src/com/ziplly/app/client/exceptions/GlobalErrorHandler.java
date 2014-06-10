package com.ziplly.app.client.exceptions;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.ErrorDefinitions.ErrorDefinition;
import com.ziplly.app.client.view.event.NeedsLoginEvent;
import com.ziplly.app.client.widget.AlertModal;
import com.ziplly.app.client.widget.MessageModal;

public class GlobalErrorHandler {
	AlertModal modal = new AlertModal();
	MessageModal mmodal = new MessageModal();
	
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
		
//		modal.showMessage(errorDef.getErrorMessage(), errorDef.getType());
		mmodal.setContent(errorDef.getErrorMessage());
		mmodal.setTitle("Error");
		mmodal.show();
		if (errorDef.getCode() == ErrorCodes.NeedsLoginError) {
		  eventBus.fireEvent(new NeedsLoginEvent());
		  return;
		}
		
		postHandle();
	}

	public void postHandle() {
  }
}
