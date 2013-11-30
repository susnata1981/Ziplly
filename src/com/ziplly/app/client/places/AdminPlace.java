package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class AdminPlace extends Place {
	
	public AdminPlace() {
	}
	
	@Prefix("admin")
	public static class Tokenizer implements PlaceTokenizer<AdminPlace> {
		@Override
		public AdminPlace getPlace(String token) {
			return new AdminPlace();
		}

		@Override
		public String getToken(AdminPlace place) {
			return "";
		}
	}
}
