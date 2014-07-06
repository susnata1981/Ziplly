package com.ziplly.app.client.places;

import java.util.Map;

import com.google.gwt.place.shared.Prefix;
import com.google.inject.Inject;

public class BusinessAccountPlace extends AccountPlace {
  public static final String TOKEN = "businessaccount";

  public BusinessAccountPlace() {
  }

  public BusinessAccountPlace(Long accountId) {
    this.setAccountId(accountId);
  }

  @Prefix("businessaccount")
  public static class Tokenizer extends BaseTokenizer<BusinessAccountPlace> {
    
    @Override
    public BusinessAccountPlace getPlace(String token) {
//      try {
//        BusinessAccountPlace place = new BusinessAccountPlace();
//        if (token != null && !"".equals(token)) {
//          tokenize(token);
//          long accountId = Long.parseLong(getTokenAt(0));
//          place.setAccountId(accountId);
//          boolean showTransaction = Boolean.parseBoolean(getTokenAt(1));
//          place.setShowTransactions(showTransaction);
//          return place;
//        }
//
//        return place;
//      } catch (Exception ex) {
//        return new BusinessAccountPlace();
//      }
      try {
        BusinessAccountPlace place = new BusinessAccountPlace();
        Map<AttributeKey, AttributeValue> params = parser.parse(token);
        
        for(AttributeKey key: params.keySet()) {
          if (key.equals(AttributeKey.TRANSACTION_VIEW_TOKEN)) {
            place.setShowTransactions(true);
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
