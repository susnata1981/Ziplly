package com.ziplly.app.client.exceptions;

import net.customware.gwt.dispatch.shared.DispatchException;

public class CouponPurchaseLimitExceededException extends DispatchException {

  private static final long serialVersionUID = 1L;
  
  public CouponPurchaseLimitExceededException(String errMsg) {
    super(errMsg);
  }
  
  public CouponPurchaseLimitExceededException() {
  }

}
