package com.ziplly.app.model;

public enum NotificationType {
	PERSONAL_MESSAGE(true),
	SECURITY_ALERT(true),
	ANNOUNCEMENT(true),
	OFFERS(true), 
	NONE(false);
	
	/*
	 * Whether it's visible to the user. 
	 */
	private boolean visible;

	private NotificationType(boolean visible) {
		this.setVisible(visible);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
