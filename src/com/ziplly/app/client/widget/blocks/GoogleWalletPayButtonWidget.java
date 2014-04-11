package com.ziplly.app.client.widget.blocks;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class GoogleWalletPayButtonWidget {
	private GoogleWalletPayButtonHandler handler;
	private String jwtToken;
	private Button btn;

	public GoogleWalletPayButtonWidget(Button btn, final String jwt, GoogleWalletPayButtonHandler handler) {
		this.btn = btn;
		this.jwtToken = jwt;
		this.handler = handler;
  }
	
	public void pay() {
		doPay(jwtToken);
	}
	
	private native void doPay(final String jwtToken) /*-{
		var that = this;
		$wnd.google.payments.inapp
				.buy({
					'jwt' : jwtToken,
					'success' : function() {
						alert('success');
						that.@com.ziplly.app.client.widget.blocks.GoogleWalletPayButtonWidget::onSuccess()();
					},
					'failure' : function() {
						alert('failure');
						that.@com.ziplly.app.client.widget.blocks.GoogleWalletPayButtonWidget::onFailure()();
					}
				});
	}-*/;

	public void onSuccess() {
		handler.onSuccess();
	};

	public void onFailure() {
		Window.alert("Failed");
		handler.onFailure();
	};

	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return btn.addClickHandler(handler);
	}
}
