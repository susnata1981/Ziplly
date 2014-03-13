package com.ziplly.app.client.exceptions;

import net.customware.gwt.dispatch.shared.DispatchException;

public class DuplicateException extends DispatchException {

	private static final long serialVersionUID = 1L;

	public DuplicateException() {
	}

	public DuplicateException(String msg) {
		super(msg);
	}
}
