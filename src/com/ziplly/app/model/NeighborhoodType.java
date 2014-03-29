package com.ziplly.app.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum NeighborhoodType {
		X(0,""), 
		XX(0,""),
		XXX(0, ""), 
		XXXX(0, ""),
//	NEIGHBORHOOD,
//	LOCALITY,
//	ADMINISTRATIVE_AREA_LEVEL_2,
//	ADMINISTRATIVE_AREA_LEVEL_1,
//	COUNTRY;
	
	NEIGHBORHOOD(3, "neighborhood"),
	SUB_LOCALITY(4, "sublocality"),
	LOCALITY(5, "locality"),
	ADMINISTRATIVE_AREA_LEVEL_3(6, "administrative_area_level_3"),
	ADMINISTRATIVE_AREA_LEVEL_2(7, "administrative_area_level_2"),
	ADMINISTRATIVE_AREA_LEVEL_1(8, "administrative_area_level_1"),
	COUNTRY(9, "country"),
	POSTAL_CODE(10, "postal_code");
	
	private String name;
	private int priority;

	private NeighborhoodType(int priority, String name) {
		this.priority = priority;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getPriority() {
		return priority;
	}

	public static NeighborhoodType getNeighborhoodType(String name) {
		for (NeighborhoodType ad : values()) {
			if (ad.getName().equalsIgnoreCase(name)) {
				return ad;
			}
		}
		return null;
	}
	
	public static List<NeighborhoodType> getSortedValues() {
		List<NeighborhoodType> types = new ArrayList<NeighborhoodType>();
		types.add(NEIGHBORHOOD);
		types.add(SUB_LOCALITY);
		types.add(LOCALITY);
		types.add(ADMINISTRATIVE_AREA_LEVEL_3);
		types.add(ADMINISTRATIVE_AREA_LEVEL_2);
		types.add(ADMINISTRATIVE_AREA_LEVEL_1);
		types.add(COUNTRY);
		types.add(POSTAL_CODE);
		return Collections.unmodifiableList(types);
	}
}
