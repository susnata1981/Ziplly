package com.ziplly.app.client.widget.blocks;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class GoogleWalletPayButtonWidget {
	private GoogleWalletPostPayButtonHandler handler;
//	private String jwtToken;
	private Button btn;

	public GoogleWalletPayButtonWidget(final Button btn, final GoogleWalletPostPayButtonHandler handler) {
		this.btn = btn;
//		this.jwtToken = jwt;
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
					'success' : function() {
						alert('success');
						that.@com.ziplly.app.client.widget.blocks.GoogleWalletPayButtonWidget::onSuccess()();
					},
					'failure' : function(result) {
						console.log(result);
						alert(result.response.errorType);
//						that.@com.ziplly.app.client.widget.blocks.GoogleWalletPayButtonWidget::test(Response)(result.response);
						that.@com.ziplly.app.client.widget.blocks.GoogleWalletPayButtonWidget::onFailure()();
					}
				});
	}-*/;

	public void onSuccess() {
		handler.onSuccess();
	};

	public void onFailure() {
		handler.onFailure();
	};

	public void test(Response o) {
		Window.alert("ET="+o.getErrorType());
	}
	
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return btn.addClickHandler(handler);
	}
	
	private static class Response extends JavaScriptObject {
		protected Response() {
		}
		
		public final native String getErrorType() /*-{
			return this.errorT
		}-*/;
	}
}
