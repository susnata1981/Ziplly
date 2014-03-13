package com.ziplly.app.model;

public enum BusinessCategory {
	RESTAURANT("Restaurant"),
	SPECIALTY_FOOD("Specialty food"),
	FASTFOOD("Fastfood"),
	FOODTRUCK("Food truck"),
	BAR("Bar"),
	CAFE("Cafe"),
	COFFEE("Cofee shop"),
	FITNESS("Health Club"),
	HOME_DECOR("Home decor"),
	SPA("Spa"),
	YOGA("Yoga studio"),
	SALOON("Saloon"),
	CONSIGNMENT("Consignment"),
	CLOTHING("Clothing"),
	RECORD("Record store"),
	OTHER("Other");

	private String name;

	BusinessCategory(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
