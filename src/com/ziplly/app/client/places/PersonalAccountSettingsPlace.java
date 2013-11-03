package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class PersonalAccountSettingsPlace extends Place {
	
	public PersonalAccountSettingsPlace() {
	}
	
	@Prefix("personalsettings")
	public static class Tokenizer implements PlaceTokenizer<PersonalAccountSettingsPlace> {
		@Override
		public PersonalAccountSettingsPlace getPlace(String token) {
			if (token != null && !"".equals(token)) {
				return new PersonalAccountSettingsPlace();
			}
			return new PersonalAccountSettingsPlace();
		}

		@Override
		public String getToken(PersonalAccountSettingsPlace place) {
			return "";
		}
	}
}
