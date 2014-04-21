package com.ziplly.app.model.overlay;

import com.google.gwt.core.client.JavaScriptObject;

public class GoogleWalletResponse extends JavaScriptObject {
	protected GoogleWalletResponse() {
	}
	
	public final native String getOrderId() /*-{
		return this.orderId;
	}-*/;
}
