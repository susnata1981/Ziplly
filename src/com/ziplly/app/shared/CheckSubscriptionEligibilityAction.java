package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class CheckSubscriptionEligibilityAction implements Action<CheckSubscriptionEligibilityResult> {
	private Long subscriptionId;

	public Long getSubscriptionId() {
	  return subscriptionId;
  }

	public void setSubscriptionId(Long subscriptionId) {
	  this.subscriptionId = subscriptionId;
  }
	
}
