package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class BusinessPlace extends Place {
	public BusinessPlace() {
	}

	@Prefix("business")
	public static class Tokenizer implements PlaceTokenizer<BusinessPlace> {
		@Override
		public BusinessPlace getPlace(String token) {
			return new BusinessPlace();
		}

		@Override
		public String getToken(BusinessPlace place) {
			return "";
		}
	}
}
