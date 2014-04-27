package com.ziplly.app.client.exceptions;

import net.customware.gwt.dispatch.shared.DispatchException;

public class CouponSoldOutException extends DispatchException {
  private static final long serialVersionUID = 1L;

	public CouponSoldOutException(String errMsg) {
		super(errMsg);
  }
	
	public CouponSoldOutException() {
  }
}
