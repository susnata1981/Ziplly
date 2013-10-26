package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class BusinessSignupPlace extends Place {
	public BusinessSignupPlace() {
	}
	
	@Prefix("businesssignup")
	public static class Tokenizer implements PlaceTokenizer<BusinessSignupPlace> {
		@Override
		public BusinessSignupPlace getPlace(String token) {
			return new BusinessSignupPlace();
		}

		@Override
		public String getToken(BusinessSignupPlace place) {
			return "";
		}
	}
}
