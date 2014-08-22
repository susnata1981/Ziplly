package com.ziplly.app.client.places;

import java.util.Map;

import com.google.gwt.place.shared.Prefix;

public class PersonalAccountPlace extends AccountPlace {
  public static final String TOKEN = "personalaccount";

  public PersonalAccountPlace(long accountId) {
    super(accountId);
  }

  public PersonalAccountPlace() {
    super(0L);
  }

  @Prefix("personalaccount")
  public static class Tokenizer extends BaseTokenizer<PersonalAccountPlace> {

    @Override
    public PersonalAccountPlace getPlace(String token) {
      try {
        PersonalAccountPlace place = new PersonalAccountPlace();
        Map<AttributeKey, AttributeValue> params = parser.parse(token);
        
        for(AttributeKey key: params.keySet()) {
          if (key.equals(AttributeKey.TRANSACTION_VIEW_TOKEN)) {
            place.setShowTransactions(Boolean.valueOf(params.get(key).value()));
          } else if (key.equals(AttributeKey.ACCOUNT_ID)) {
            place.setAccountId(Long.parseLong(params.get(key).value()));
          }
        }
        
        return place;
      } catch (Exception ex) {
        return new PersonalAccountPlace();
      }
    }
    
    @Override
    public String getToken(PersonalAccountPlace place) {
      return PlaceUtils.getPlaceToken(place);
    }
  }
}
