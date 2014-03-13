package com.ziplly.app.client.exceptions;

import net.customware.gwt.dispatch.shared.DispatchException;

public class AccessError extends DispatchException {
	private static final long serialVersionUID = 1L;

	public AccessError() {
	}

	public AccessError(String msg) {
		super(msg);
	}
}
