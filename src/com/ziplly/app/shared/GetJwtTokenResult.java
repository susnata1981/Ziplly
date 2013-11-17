package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Result;

public class GetJwtTokenResult implements Result {
	private String token;

	public GetJwtTokenResult() {
	}
	
	public GetJwtTokenResult(String token) {
		this.setToken(token);
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
