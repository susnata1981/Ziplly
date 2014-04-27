package com.ziplly.app.client.exceptions;

import net.customware.gwt.dispatch.shared.DispatchException;

public class InternalException extends DispatchException {
	private static final long serialVersionUID = 1L;

	public InternalException(String msg) {
		super(msg);
	}

	public InternalException() {
	}
}
