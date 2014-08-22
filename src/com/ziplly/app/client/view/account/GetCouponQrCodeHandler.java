package com.ziplly.app.client.view.account;

import com.google.gwt.event.shared.EventBus;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.view.coupon.CouponTransactionView;
import com.ziplly.app.shared.GetCouponQRCodeUrlResult;

public class GetCouponQrCodeHandler extends DispatcherCallbackAsync<GetCouponQRCodeUrlResult> {
  private CouponTransactionView view;

  public GetCouponQrCodeHandler(EventBus eventBus, CouponTransactionView view) {
    super(eventBus);
    this.view = view;
  }

  @Override
  public void onSuccess(GetCouponQRCodeUrlResult result) {
    view.displayQrCode(result.getUrl());
  }
}
