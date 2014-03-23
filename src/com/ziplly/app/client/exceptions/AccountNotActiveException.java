package com.ziplly.app.client.exceptions;

import net.customware.gwt.dispatch.shared.DispatchException;

public class AccountNotActiveException extends DispatchException {
	private static final long serialVersionUID = 1L;

	public AccountNotActiveException(String msg) {
		super(msg);
	}

	public AccountNotActiveException() {
	}
}
