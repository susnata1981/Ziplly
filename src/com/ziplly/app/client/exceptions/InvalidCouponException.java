package com.ziplly.app.client.exceptions;

import net.customware.gwt.dispatch.shared.DispatchException;

public class InvalidCouponException extends DispatchException {

  private static final long serialVersionUID = 1L;

  public InvalidCouponException() {
  }
 
  public InvalidCouponException(String errorMsg) {
  	super(errorMsg);
  }
}