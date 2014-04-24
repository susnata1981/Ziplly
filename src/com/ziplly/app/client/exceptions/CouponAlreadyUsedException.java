package com.ziplly.app.client.exceptions;

import net.customware.gwt.dispatch.shared.DispatchException;

public class CouponAlreadyUsedException extends DispatchException {
  private static final long serialVersionUID = 1L;

	public CouponAlreadyUsedException() {
  }
	
	public CouponAlreadyUsedException(String errorMsg) {
		super(errorMsg);
  }
}
