package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Result;

public class CheckSubscriptionEligibilityResult implements Result {
	private String token;
	private SubscriptionEligibilityStatus eligibilityStatus;
	
	public String getToken() {
	  return token;
  }

	public void setToken(String token) {
	  this.token = token;
  }

	public SubscriptionEligibilityStatus getEligibilityStatus() {
	  return eligibilityStatus;
  }

	public void setEligibilityStatus(SubscriptionEligibilityStatus eligibilityStatus) {
	  this.eligibilityStatus = eligibilityStatus;
  }
}
