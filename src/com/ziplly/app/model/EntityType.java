package com.ziplly.app.model;

public enum EntityType {
	PERSONAL_ACCOUNT("personal"),
	BUSINESS_ACCOUNT("business"), /* Will change to business */
	PUBLISHER_ACCOUNT("business"); /* Will change to business */
	
	private String type;

	EntityType(String dtype) {
		this.setType(dtype);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
