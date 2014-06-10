package com.ziplly.app.model.overlay;

import com.google.gwt.core.client.JavaScriptObject;

public class GoogleWalletPayloadRequest extends JavaScriptObject {
	protected GoogleWalletPayloadRequest() {
	}
	
	public final native String getName() /*-{
		return this.name;
	}-*/;
	
	public final native String getDescription() /*-{
		return this.description;
	}-*/;
	
	public final native double getPrice() /*-{
		return this.price;
	}-*/;
	
	public final native String getCurrencyCode() /*-{
		return this.currencyCode;
	}-*/;
	
	public final native int getCouponId() /*-{
		return this.couponId;
	}-*/;
	
	public final native int getBuyerId() /*-{
		return this.buyerId;
	}-*/;
	
	public final native int getPurchaseCouponId() /*-{
		return this.purchaseCouponId;
	}-*/;
}
