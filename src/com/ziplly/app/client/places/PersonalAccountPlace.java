package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class PersonalAccountPlace extends Place {
	public PersonalAccountPlace() {
	}
	
	@Prefix("personalaccount")
	public static class Tokenizer implements PlaceTokenizer<PersonalAccountPlace> {
		@Override
		public PersonalAccountPlace getPlace(String token) {
			if (token != null && !"".equals(token)) {
				return new PersonalAccountPlace();
			}
			return new PersonalAccountPlace();
		}

		@Override
		public String getToken(PersonalAccountPlace place) {
			return "";
		}
	}	
}
