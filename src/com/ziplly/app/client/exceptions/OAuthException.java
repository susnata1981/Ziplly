package com.ziplly.app.client.exceptions;

import net.customware.gwt.dispatch.shared.DispatchException;

public class OAuthException extends DispatchException {
	private static final long serialVersionUID = 1L;

	public OAuthException(String msg) {
		super(msg);
	}
	
	public OAuthException() {
	}
}
