package com.ziplly.app.model;

public enum BusinessType {
	COMMERCIAL("Commercial"),
	NON_PROFIT("Non profile");
	
	private String name;

	private BusinessType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
