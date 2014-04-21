package com.ziplly.app.model.overlay;

import com.google.gwt.core.client.JavaScriptObject;

public class GoogleWalletFailureResult extends JavaScriptObject {
	protected GoogleWalletFailureResult() {
	}
	
	public final native String getJwt() /*-{
		return this.jwt;
	}-*/;
	
	public final native GoogleWalletRequest getRequest() /*-{
		return this.request;
	}-*/;
	
	public final native GoogleWalletResponse getResponse() /*-{
		return this.response;
	}-*/;
	
	public final String print() {
		return getRequest().getAud() +":" + 
				getRequest().getRequest().getDescription() + ":" + 
				getRequest().getRequest().getBuyerId();
	}
}
