package com.ziplly.app.client.places;

import java.util.Map;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.Prefix;

public class ConversationPlace extends Place {
	private long conversationId;

	public ConversationPlace() {
	}

	public ConversationPlace(long conversationId) {
		this.setConversationId(conversationId);
	}

	public long getConversationId() {
		return conversationId;
	}

	public void setConversationId(Long conversationId) {
		this.conversationId = conversationId;
	}

//	@Prefix("conversation")
//	public static class Tokenizer implements PlaceTokenizer<ConversationPlace> {
//		@Override
//		public ConversationPlace getPlace(String token) {
//			if (token != null) {
//				try {
//					Long id = Long.parseLong(token);
//					return new ConversationPlace(id);
//				} catch (NumberFormatException nfe) {
//					// ignore, return default place
//				}
//			}
//			return new ConversationPlace();
//		}
//
//		@Override
//		public String getToken(ConversationPlace place) {
//			if (place.getConversationId() != null) {
//				return place.getConversationId().toString();
//			}
//			return "";
//		}
//	}
	
	@Prefix("conversation")
	public static class Tokenizer extends BaseTokenizer<ConversationPlace> {

    @Override
    public ConversationPlace getPlace(String token) {
      try {
        ConversationPlace place = new ConversationPlace();
        Map<AttributeKey, AttributeValue> params = parser.parse(token);
        
        for(AttributeKey key: params.keySet()) {
          if (key.equals(AttributeKey.CONVERSATION_ID)) {
            place.setConversationId(Long.parseLong(params.get(key).value()));
          }
        }
        
        return place;
      } catch (Exception ex) {
        return new ConversationPlace();
      }
    }

    @Override
    public String getToken(ConversationPlace place) {
      return PlaceUtils.getPlaceToken(place);
    }
	  
	}
}
