package com.ziplly.app.client.exceptions;

import net.customware.gwt.dispatch.shared.DispatchException;

public class AccountAlreadySubscribedException extends DispatchException {
	private static final long serialVersionUID = 1L;

	public AccountAlreadySubscribedException() {
	}
	
	public AccountAlreadySubscribedException(String msg) {
		super(msg);
	}
}
