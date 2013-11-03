package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class BusinessAccountSettingsPlace extends Place {
	public BusinessAccountSettingsPlace() {
	}
	
	@Prefix("businesssettings")
	public static class Tokenizer implements PlaceTokenizer<BusinessAccountSettingsPlace> {
		@Override
		public BusinessAccountSettingsPlace getPlace(String token) {
			return new BusinessAccountSettingsPlace();
		}

		@Override
		public String getToken(BusinessAccountSettingsPlace place) {
			return "";
		}
	}
}
