package com.ziplly.app.client.exceptions;

import net.customware.gwt.dispatch.shared.DispatchException;

public class InternalError extends DispatchException {
	private static final long serialVersionUID = 1L;

	public InternalError(String msg) {
		super(msg);
	}

	public InternalError() {
	}
}
