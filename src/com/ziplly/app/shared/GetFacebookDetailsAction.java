package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

import com.ziplly.app.client.dispatcher.Cacheable;

public class GetFacebookDetailsAction implements Action<GetFacebookDetailsResult>, Cacheable {
	private String code;

	public GetFacebookDetailsAction() {
	}

	public GetFacebookDetailsAction(String code) {
		this.setCode(code);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
