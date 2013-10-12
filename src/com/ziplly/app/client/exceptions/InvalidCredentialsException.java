package com.ziplly.app.client.exceptions;

import net.customware.gwt.dispatch.shared.DispatchException;

public class InvalidCredentialsException extends DispatchException {
	private static final long serialVersionUID = 1L;
	
	public InvalidCredentialsException(String msg) {
		super(msg);
	}
	
	public InvalidCredentialsException() {
	}
}
