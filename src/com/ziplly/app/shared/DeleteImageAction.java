package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class DeleteImageAction implements Action<DeleteImageResult> {

	private String imageUrl;

	public DeleteImageAction() {
	}

	public DeleteImageAction(String url) {
		this.setImageUrl(url);
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
}
