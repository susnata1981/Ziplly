package com.ziplly.app.model;

public enum AccountType {
	PERSONAL("personal"),BUSINESS("BusinessAccount"),NON_PROFIT("NonProfit"),NONE("none");
	
	private String name;

	private AccountType(String name) {
		this.setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
