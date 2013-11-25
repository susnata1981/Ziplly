package com.ziplly.app.client.exceptions;

import net.customware.gwt.dispatch.shared.DispatchException;

public class UsageLimitExceededException extends DispatchException {
	private static final long serialVersionUID = 1L;

	public UsageLimitExceededException() {
	}
	
	public UsageLimitExceededException(String msg) {
		super(msg);
	}
}
