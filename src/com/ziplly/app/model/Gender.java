package com.ziplly.app.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Gender {
	NOT_SPECIFIED("Not specified", true, false),
	MALE("Male", true, true), 
	FEMALE("Female", true, true),
	ALL("All", false, true);
	
	private String name;
	private boolean appearsOnSignup;
	private boolean searchFilter;

	private Gender(String text, boolean appearsOnSignup, boolean searchFilter) {
		this.setName(text);
		this.setAppearsOnSignup(appearsOnSignup);
		this.setSearchFilter(searchFilter);
	}

	public static List<Gender> getValuesForSignup() {
		List<Gender> result = new ArrayList<Gender>();
		for(Gender g : values()) {
			if (g.isAppearsOnSignup()) {
				result.add(g);
			}
		}
		return result;
	}
	
	public static List<Gender> getValuesForSearch() {
		List<Gender> result = new ArrayList<Gender>();
		for(Gender g : values()) {
			if (g.isSearchFilter()) {
				result.add(g);
			}
		}
		return result;
	}
	
	public static List<Gender> getAllValues() {
		return Arrays.asList(values());
	}

	public boolean isAppearsOnSignup() {
		return appearsOnSignup;
	}

	public void setAppearsOnSignup(boolean appearsOnSignup) {
		this.appearsOnSignup = appearsOnSignup;
	}

	public boolean isSearchFilter() {
		return searchFilter;
	}

	public void setSearchFilter(boolean searchFilter) {
		this.searchFilter = searchFilter;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
