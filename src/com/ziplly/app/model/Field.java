package com.ziplly.app.model;

import java.io.Serializable;

public enum Field implements Serializable {
	FIRSTNAME("firstname"),
	LASTNAME("lastname"),
	EMAIL("email"),
	ID("id"),
	CATEGORY_KEY("category"),
	ACCOUNT_KEY("account");

	private String name;

	Field(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
