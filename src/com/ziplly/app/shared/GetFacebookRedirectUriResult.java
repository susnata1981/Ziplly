package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Result;

public class GetFacebookRedirectUriResult implements Result {
	private String redirectUrl;

	public GetFacebookRedirectUriResult() {
	}
	
	public GetFacebookRedirectUriResult(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	
	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
}
