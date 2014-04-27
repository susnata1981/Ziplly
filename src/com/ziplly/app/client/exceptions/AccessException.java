package com.ziplly.app.client.exceptions;

import net.customware.gwt.dispatch.shared.DispatchException;

public class AccessException extends DispatchException {
	private static final long serialVersionUID = 1L;

	public AccessException() {
	}

	public AccessException(String msg) {
		super(msg);
	}
}
