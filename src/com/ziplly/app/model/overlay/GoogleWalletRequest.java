package com.ziplly.app.model.overlay;

import com.google.gwt.core.client.JavaScriptObject;

public class GoogleWalletRequest extends JavaScriptObject {
	protected GoogleWalletRequest() {
	}

	public final native String getAud() /*-{
		return this.aud;
	}-*/;

	public final native String getExp() /*-{
		return this.exp;
	}-*/;

	public final native String getIat() /*-{
		return this.iat;
	}-*/;

	public final native String getIss() /*-{
		return this.iss;
	}-*/;

	public final native GoogleWalletPayloadRequest getRequest() /*-{
		return this.request;
	}-*/;

	public final native String getTyp() /*-{
		return this.type;
	}-*/;
}
