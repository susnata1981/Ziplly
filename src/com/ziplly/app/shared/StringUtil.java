package com.ziplly.app.shared;

public class StringUtil {

	public static String capitalize(String line) {
		if (line != null && line.length() > 0) {
			return Character.toUpperCase(line.charAt(0)) + line.substring(1).toLowerCase();
		}
		throw new RuntimeException("Invalid arguement to capitalize");
	}
}
