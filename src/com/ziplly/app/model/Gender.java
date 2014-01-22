package com.ziplly.app.model;

import java.util.Arrays;
import java.util.List;

public enum Gender {
	MALE, 
	FEMALE,
	ALL;
	
	public static List<Gender> getDistinctValues() {
		return Arrays.asList(MALE, FEMALE);
	}
	
	public static List<Gender> getAllValues() {
		return Arrays.asList(values());
	}
}
