package com.ziplly.app.client.activities.util;

import com.ziplly.app.client.widget.blocks.GoogleWalletPostPayButtonHandler;
import com.ziplly.app.model.overlay.GoogleWalletFailureResult;
import com.ziplly.app.model.overlay.GoogleWalletSuccessResult;

public class PaymentFlow {
  private GoogleWalletPostPayButtonHandler handler;

  public PaymentFlow(GoogleWalletPostPayButtonHandler handler) {
    this.handler = handler;
  }
  
  public native void doPay(final String jwtToken) /*-{
		var that = this;
		$wnd.google.payments.inapp
				.buy({
					'jwt' : jwtToken,
					'success' : function(result) {
						that.@com.ziplly.app.client.activities.util.PaymentFlow::onSuccess(Lcom/ziplly/app/model/overlay/GoogleWalletSuccessResult;)(result);
					},
					'failure' : function(result) {
						that.@com.ziplly.app.client.activities.util.PaymentFlow::onFailure(Lcom/ziplly/app/model/overlay/GoogleWalletFailureResult;)(result);
					}
				});
  }-*/;

  private void onSuccess(GoogleWalletSuccessResult result) {
    if (handler != null) {
      handler.onSuccess(result);
    }
  };

  private void onFailure(GoogleWalletFailureResult result) {
    if (handler != null) {
      handler.onFailure(result);
    }
  };
}
