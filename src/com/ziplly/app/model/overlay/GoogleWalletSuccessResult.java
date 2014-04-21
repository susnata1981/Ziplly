package com.ziplly.app.model.overlay;

import com.google.gwt.core.client.JavaScriptObject;

public class GoogleWalletSuccessResult extends JavaScriptObject {
	protected GoogleWalletSuccessResult() {
	}
	
	public final native String getJwt() /*-{
		return this.jwt;
	}-*/;
	
	public final native GoogleWalletPayloadRequest getRequest() /*-{
		return this.request;
	}-*/;
	
	public final native GoogleWalletResponse getResponse() /*-{
		return this.response;
	}-*/;
	
	public final String print() {
		return getRequest().getDescription() + ":" + 
				getRequest().getBuyerId();
	}
}
