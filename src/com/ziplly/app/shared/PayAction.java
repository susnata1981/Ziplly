package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class PayAction implements Action<PayResult> {
	private Long subscriptionId;
	
	public PayAction() {
	}

	public PayAction(Long subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public Long getSubscriptionId() {
	  return subscriptionId;
  }

	public void setSubscriptionId(Long subscriptionId) {
	  this.subscriptionId = subscriptionId;
  }
}
