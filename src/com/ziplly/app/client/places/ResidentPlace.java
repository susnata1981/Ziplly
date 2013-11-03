package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ResidentPlace extends Place {
		public ResidentPlace() {
		}
		
		@Prefix("residents")
		public static class Tokenizer implements PlaceTokenizer<ResidentPlace> {
			@Override
			public ResidentPlace getPlace(String token) {
				return new ResidentPlace();
			}

			@Override
			public String getToken(ResidentPlace place) {
				return "";
			}
		}
}
