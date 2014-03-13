package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;

public class OAuthPlace extends Place {
	private String code;

	public OAuthPlace(String code) {
		this.setCode(code);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
