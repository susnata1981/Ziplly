package com.ziplly.app.client.view.common.action;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.ziplly.app.client.resource.StringDefinitions;
import com.ziplly.app.client.widget.TweetWidget;
import com.ziplly.app.shared.CancelCouponPurchaseResult;

public class CancelTransactionHandler extends AbstractClientHandler<CancelCouponPurchaseResult> {

  public CancelTransactionHandler(EventBus eventBus,
      TweetWidget widget,
      StringDefinitions stringDefinitions) {
    super(eventBus, widget, stringDefinitions);
  }

  @Override
  public void onSuccess(CancelCouponPurchaseResult result) {
    widget.displayMessage(stringDefinitions.paymentCanclled(), AlertType.WARNING);
  }
}
