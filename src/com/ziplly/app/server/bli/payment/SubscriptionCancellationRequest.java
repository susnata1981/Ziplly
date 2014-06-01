package com.ziplly.app.server.bli.payment;

import net.customware.gwt.dispatch.shared.DispatchException;

import com.ziplly.app.server.bli.SubscriptionBLI;

public class SubscriptionCancellationRequest extends BaseRequest {
  private SubscriptionBLI subscriptionBli;
  private String orderId;
  
  public SubscriptionCancellationRequest(SubscriptionBLI subscriptionBli) {
    this.subscriptionBli = subscriptionBli;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  @Override
  public void completeTransaction() throws NumberFormatException, DispatchException, Exception {
    subscriptionBli.cancelOrder(getOrderId());
  }
}
