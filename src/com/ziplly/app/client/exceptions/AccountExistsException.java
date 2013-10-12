package com.ziplly.app.client.exceptions;

import net.customware.gwt.dispatch.shared.DispatchException;

public class AccountExistsException extends DispatchException {
	private static final long serialVersionUID = 1L;
	
	public AccountExistsException() {
	}
	
	public AccountExistsException(String msg) {
		super(msg);
	}

}
