package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.overlay.SubscriptionDTO;

public class PayResult implements Result {
	private SubscriptionDTO subscription;
	
	public PayResult() {
	}

	public SubscriptionDTO getSubscription() {
	  return subscription;
  }

	public void setSubscription(SubscriptionDTO subscription) {
	  this.subscription = subscription;
  }
}
