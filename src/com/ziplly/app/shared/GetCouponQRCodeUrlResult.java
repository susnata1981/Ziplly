package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Result;

public class GetCouponQRCodeUrlResult implements Result {
	private String url;
	
	public GetCouponQRCodeUrlResult() {
  }
	
	public GetCouponQRCodeUrlResult(String url) {
		this.setUrl(url);
  }

	public String getUrl() {
	  return url;
  }

	public void setUrl(String url) {
	  this.url = url;
  }
}
