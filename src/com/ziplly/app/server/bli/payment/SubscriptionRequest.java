package com.ziplly.app.server.bli.payment;

import com.google.inject.Inject;
import com.ziplly.app.server.bli.SubscriptionBLI;

import net.customware.gwt.dispatch.shared.DispatchException;

public class SubscriptionRequest extends BaseRequest {
	private Long sellerId;
	private Long subscriptionId;
	private SubscriptionBLI subscriptionBli;

	@Inject
	public SubscriptionRequest(SubscriptionBLI subscriptionBli) {
		this.subscriptionBli = subscriptionBli;
  }
	
	public Long getSellerId() {
	  return sellerId;
  }
	public void setSellerId(Long sellerId) {
	  this.sellerId = sellerId;
  }
	public Long getSubscriptionId() {
	  return subscriptionId;
  }
	public void setSubscriptionId(Long subscriptionId) {
	  this.subscriptionId = subscriptionId;
  }

	@Override
  public String toString() {
	  return super.toString() + "SubscriptionRequest [sellerId=" + sellerId + ", subscriptionId=" + subscriptionId;
  }

	@Override
  public void completeTransaction() throws NumberFormatException, DispatchException, Exception {
		subscriptionBli.completeTransaction(sellerId, subscriptionId, getOrderId());
  }
}
