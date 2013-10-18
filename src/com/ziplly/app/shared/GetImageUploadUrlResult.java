package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Result;

public class GetImageUploadUrlResult implements Result {
	private String imageUrl;

	public GetImageUploadUrlResult() {
	}
	
	public GetImageUploadUrlResult(String url) {
		this.setImageUrl(url);
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
}
