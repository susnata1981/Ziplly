package com.ziplly.app.client.places;

import java.util.Map;

import com.google.gwt.place.shared.Prefix;

public class BusinessAccountPlace extends AccountPlace {
  public static final String TOKEN = "businessaccount";

  public BusinessAccountPlace() {
    super(0L);
  }

  public BusinessAccountPlace(Long accountId) {
    super(accountId);
  }

  @Prefix("businessaccount")
  public static class Tokenizer extends BaseTokenizer<BusinessAccountPlace> {
    
    @Override
    public BusinessAccountPlace getPlace(String token) {
      try {
        BusinessAccountPlace place = new BusinessAccountPlace();
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
        return new BusinessAccountPlace();
      }
    }

    @Override
    public String getToken(BusinessAccountPlace place) {
      return PlaceUtils.getPlaceToken(place);
    }
  }
}
