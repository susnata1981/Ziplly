package com.ziplly.app.model;

import java.util.Arrays;
import java.util.List;

public class InterestList {
//	public enum Activity {
//		OUTDOOR,
//		INDOOR,
//		SCIENCE,
//		ELECTRONICS,
//		A
//	}
//	
//	private static InterestList INSTANCE = new InterestList();
//	Map<Activity, List<String>> interests = new HashMap<Activity, List<String>>();
//
//	protected InterestList() {
//		init();
//	}
//
//	public static InterestList getInstance() {
//		return INSTANCE;
//	}
//
//	private void init() {
//		List<String> outdoors = Arrays.asList("Badminton","Boxing",
//				"Soccer", "Color Guard", "Cheerleading", "Cricket",
//				"Cubing", "Bridge", "Billiards", "Darts", "Dancing", "Fencing",
//				"Gaming", "Go", "Gymnastics", "Martial arts", "Poker",
//				"Programming", "Football", "Handball", "Weightlifting",
//				"Volleyball");
//		List<String> indoors = Arrays.asList("Board Games","Indoor Soccer");
//		interests.put(Activity.OUTDOOR, outdoors);
//		interests.put(Activity.INDOOR, indoors);
//	}
//
//	public Map<Activity, List<String>> getInterests() {
//		return interests;
//	}
	
	List<String> hobbies;
	
	void init() {
		hobbies = Arrays.asList(
				"Outdoors", "Indoors", "Science", "Arts & Crafts", "Sports", "Performing Arts", "Collecting",
				"Music","Pets","Food");
	}
	
	List<String> getHobbies() {
		return hobbies;
	}
}
