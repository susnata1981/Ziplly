package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ConversationPlace extends Place {

	private Long conversationId;

	public ConversationPlace() {
	}

	public ConversationPlace(Long conversationId) {
		this.setConversationId(conversationId);
	}

	public Long getConversationId() {
		return conversationId;
	}

	public void setConversationId(Long conversationId) {
		this.conversationId = conversationId;
	}

	@Prefix("conversation")
	public static class Tokenizer implements PlaceTokenizer<ConversationPlace> {
		@Override
		public ConversationPlace getPlace(String token) {
			if (token != null) {
				try {
					Long id = Long.parseLong(token);
					return new ConversationPlace(id);
				} catch (NumberFormatException nfe) {
					// ignore, return default place
				}
			}
			return new ConversationPlace();
		}

		@Override
		public String getToken(ConversationPlace place) {
			if (place.getConversationId() != null) {
				return place.getConversationId().toString();
			}
			return "";
		}
	}
}
