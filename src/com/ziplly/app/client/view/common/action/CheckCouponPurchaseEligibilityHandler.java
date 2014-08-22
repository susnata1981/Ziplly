package com.ziplly.app.client.view.common.action;

import com.google.gwt.event.shared.EventBus;
import com.ziplly.app.client.resource.StringDefinitions;
import com.ziplly.app.client.widget.TweetWidget;
import com.ziplly.app.shared.CheckBuyerEligibilityForCouponResult;

public class CheckCouponPurchaseEligibilityHandler extends AbstractClientHandler<CheckBuyerEligibilityForCouponResult> {

  public CheckCouponPurchaseEligibilityHandler(EventBus eventBus,
      TweetWidget widget,
      StringDefinitions stringDefinitions) {
    super(eventBus, widget, stringDefinitions);
  }

  @Override
  public void onSuccess(CheckBuyerEligibilityForCouponResult result) {
    widget.initiatePay(result.getJwtToken());
  }
}
