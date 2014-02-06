package com.ziplly.app.model;


public enum Activity {
	OUTDOOR("Outdoor"),
	INDOOR("Indoor"),
	SPORTS("Sports"),
	COOKING("Cooking"),
	HIKING("Hiking"),
	RUNNING("Running"),
	BIKING("Biking"),
	ELECTRONICS("Electronics"),
	MOVIES("Movies");
	
	private String name;

	Activity(String name) {
		this.name = name;
	}
	
	public String getActivityName() {
		return name;
	}
}
