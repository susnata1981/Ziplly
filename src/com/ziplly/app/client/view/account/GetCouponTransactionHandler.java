package com.ziplly.app.client.view.account;

import com.google.web.bindery.event.shared.EventBus;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.view.coupon.CouponTransactionView;
import com.ziplly.app.shared.GetCouponTransactionResult;

public class GetCouponTransactionHandler extends DispatcherCallbackAsync<GetCouponTransactionResult> {
  private CouponTransactionView view;

  public GetCouponTransactionHandler(EventBus eventBus, CouponTransactionView view) {
    super(eventBus);
    this.view = view;
  }

  @Override
  public void onSuccess(GetCouponTransactionResult result) {
    System.out.println("GetCouponTransactionResult = "+result.getTotalTransactions());
    view.displayPurchasedCoupons(result.getPurchasedCoupons());
    view.setCouponTransactionCount(result.getTotalTransactions());
  }
}
