package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Result;

public class CheckBuyerEligibilityForCouponResult implements Result {
	private String jwtToken;

	public String getJwtToken() {
	  return jwtToken;
  }

	public void setJwtToken(String jwtToken) {
	  this.jwtToken = jwtToken;
  }
}
