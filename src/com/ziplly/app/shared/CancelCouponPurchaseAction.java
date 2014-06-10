package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class CancelCouponPurchaseAction implements Action<CancelCouponPurchaseResult> {
  private long purchaseCouponId;

  public long getPurchaseCouponId() {
    return purchaseCouponId;
  }

  public void setPurchaseCouponId(long purchaseCouponId) {
    this.purchaseCouponId = purchaseCouponId;
  }
}
