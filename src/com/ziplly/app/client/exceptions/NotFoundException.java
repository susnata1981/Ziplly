package com.ziplly.app.client.exceptions;

import net.customware.gwt.dispatch.shared.DispatchException;

public class NotFoundException extends DispatchException {
	private static final long serialVersionUID = 1L;

	public NotFoundException() {
	}

	public NotFoundException(String msg) {
		super(msg);
	}
}
