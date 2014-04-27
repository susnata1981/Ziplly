package com.ziplly.app.client.exceptions;

import com.ziplly.app.client.exceptions.ErrorDefinitions.ErrorDefinition;
import com.ziplly.app.client.widget.AlertModal;

public class GlobalErrorHandler {
	AlertModal modal = new AlertModal();

	public void handlerError(Throwable th) {
		ErrorDefinition<?> errorDef = ErrorDefinitions.getErrorDefinition(th.getClass());
		if (errorDef == null) {
			return;
		}
		
		modal.showMessage(errorDef.getErrorMessage(), errorDef.getType());
		postHandle();
	}

	public void postHandle() {
  }
}
