package com.ziplly.app.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterestList {
	public enum Activity {
		OUTDOOR,
		INDOOR
	}
	
	private static InterestList INSTANCE = new InterestList();
	Map<Activity, List<String>> interests = new HashMap<Activity, List<String>>();

	protected InterestList() {
		init();
	}

	public static InterestList getInstance() {
		return INSTANCE;
	}

	private void init() {
		List<String> outdoors = Arrays.asList("Badminton","Boxing",
				"Soccer", "Color Guard", "Cheerleading", "Cricket",
				"Cubing", "Bridge", "Billiards", "Darts", "Dancing", "Fencing",
				"Gaming", "Go", "Gymnastics", "Martial arts", "Poker",
				"Programming", "Football", "Handball", "Weightlifting",
				"Volleyball");
		List<String> indoors = Arrays.asList("Board Games","Indoor Soccer");
		interests.put(Activity.OUTDOOR, outdoors);
		interests.put(Activity.INDOOR, indoors);
	}

	public Map<Activity, List<String>> getInterests() {
		return interests;
	}
}
