package com.ziplly.app.model;

public enum NotificationType {
	PERSONAL_MESSAGE("Personal message", true),
	SECURITY_ALERT("Security alert", true),
	ANNOUNCEMENT("Announcement", true),
	OFFERS("Offer", true), 
	NONE("None", false);
	
	/*
	 * Whether it's visible to the user. 
	 */
	private boolean visible;
	private String notificationName;
	
	private NotificationType(String name, boolean visible) {
		this.setNotificationName(name);
		this.setVisible(visible);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getNotificationName() {
		return notificationName;
	}

	public void setNotificationName(String notificationName) {
		this.notificationName = notificationName;
	}
}
