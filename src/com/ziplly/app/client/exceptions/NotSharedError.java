package com.ziplly.app.client.exceptions;

import net.customware.gwt.dispatch.shared.DispatchException;

public class NotSharedError extends DispatchException {
	private static final long serialVersionUID = 1L;

	public NotSharedError() {
	}

	public NotSharedError(String msg) {
		super(msg);
	}
}
