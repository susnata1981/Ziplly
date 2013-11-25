package com.ziplly.app.model.overlay;

import com.google.gwt.core.client.JavaScriptObject;

public class AddressComponent extends JavaScriptObject {
	protected AddressComponent() {
	}
	
	public final native String getLongName() /*-{
		return this.long_name;
	}-*/;
	
	public final native String getShortName() /*-{
		return this.short_name;
	}-*/;
	
	public final native String getType() /*-{
		return this.types[0];
	}-*/;
}
