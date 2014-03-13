package com.ziplly.app.model;

public enum EntityType {
	PERSONAL_ACCOUNT("personal", "PersonalAccount"), BUSINESS_ACCOUNT("business", "BusinessAccount"), /*
																																																		 * Will
																																																		 * change
																																																		 * to
																																																		 * business
																																																		 */
	PUBLISHER_ACCOUNT("business", "BusinessAccount"); /* Will change to business */

	private String type;
	private String clazz;

	EntityType(String dtype, String clazz) {
		this.setType(dtype);
		this.setClazz(clazz);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
}
