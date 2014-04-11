package com.ziplly.app.client.exceptions;

import net.customware.gwt.dispatch.shared.DispatchException;

public class SoldOutException extends DispatchException {
  private static final long serialVersionUID = 1L;

	public SoldOutException(String errMsg) {
		super(errMsg);
  }
	
	public SoldOutException() {
  }
}
