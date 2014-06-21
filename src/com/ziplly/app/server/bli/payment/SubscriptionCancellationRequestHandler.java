package com.ziplly.app.server.bli.payment;

import net.customware.gwt.dispatch.shared.DispatchException;

import com.ziplly.app.server.bli.SubscriptionBLI;

public class SubscriptionCancellationRequestHandler extends AbstractRequestHandler {
  private SubscriptionBLI subscriptionBli;
  private String orderId;
  
  public SubscriptionCancellationRequestHandler(SubscriptionBLI subscriptionBli) {
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
