package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ConversationPlace extends Place {
	
	public ConversationPlace() {
	}
	
	@Prefix("conversations")
	public static class Tokenizer implements PlaceTokenizer<ConversationPlace> {
		@Override
		public ConversationPlace getPlace(String token) {
			return new ConversationPlace();
		}

		@Override
		public String getToken(ConversationPlace place) {
			return "";
		}
	}
}
