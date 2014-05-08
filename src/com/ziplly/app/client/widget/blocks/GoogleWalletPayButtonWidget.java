package com.ziplly.app.client.widget.blocks;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.ziplly.app.model.overlay.GoogleWalletFailureResult;
import com.ziplly.app.model.overlay.GoogleWalletSuccessResult;

public class GoogleWalletPayButtonWidget {
	private GoogleWalletPostPayButtonHandler handler;
	private Button btn;

	public GoogleWalletPayButtonWidget(final Button btn, 
			final GoogleWalletPostPayButtonHandler handler) {
		
		this.btn = btn;
		this.handler = handler;
  }
	
	public void pay(String jwtToken) {
		doPay(jwtToken);
	}
	
	private native void doPay(final String jwtToken) /*-{
		var that = this;
		$wnd.google.payments.inapp
				.buy({
					'jwt' : jwtToken,
					'success' : function(result) {
						console.log(result);
						that.@com.ziplly.app.client.widget.blocks.GoogleWalletPayButtonWidget::onSuccess(Lcom/ziplly/app/model/overlay/GoogleWalletSuccessResult;)(result);
					},
					'failure' : function(result) {
						console.log(result);
						alert(result.response.errorType);
						that.@com.ziplly.app.client.widget.blocks.GoogleWalletPayButtonWidget::onFailure(Lcom/ziplly/app/model/overlay/GoogleWalletFailureResult;)(result);
					}
				});
	}-*/;

	public void onSuccess(GoogleWalletSuccessResult result) {
		System.out.println(result.print());
		handler.onSuccess(result);
	};

	public void onFailure(GoogleWalletFailureResult result) {
		System.out.println(result.print());
		handler.onFailure(result);
	};

	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return btn.addClickHandler(handler);
	}
}
