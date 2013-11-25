package com.ziplly.app.model;

public enum NotificationAction {
	EMAIL("email"),
	NO_EMAIL("no email");
	
	private String name;

	NotificationAction(String name) {
		this.setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
