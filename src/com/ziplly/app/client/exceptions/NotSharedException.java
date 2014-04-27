package com.ziplly.app.client.exceptions;

import net.customware.gwt.dispatch.shared.DispatchException;

public class NotSharedException extends DispatchException {
	private static final long serialVersionUID = 1L;

	public NotSharedException() {
	}

	public NotSharedException(String msg) {
		super(msg);
	}
}
