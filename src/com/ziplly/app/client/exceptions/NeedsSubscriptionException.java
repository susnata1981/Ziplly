package com.ziplly.app.client.exceptions;

import net.customware.gwt.dispatch.shared.DispatchException;

public class NeedsSubscriptionException extends DispatchException {
	private static final long serialVersionUID = 1L;

	public NeedsSubscriptionException() {
	}

	public NeedsSubscriptionException(String msg) {
		super(msg);
	}

}
