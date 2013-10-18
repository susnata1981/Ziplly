package com.ziplly.app.client.exceptions;

import net.customware.gwt.dispatch.shared.DispatchException;

public class NeedsLoginException extends DispatchException{
	private static final long serialVersionUID = 1L;

	public NeedsLoginException(String msg) {
		super(msg);
	}
	
	public NeedsLoginException() {
	}
}
