package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Prefix;

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
      try {
        BusinessAccountPlace place = new BusinessAccountPlace();
        if (token != null && !"".equals(token)) {
          tokenize(token);
          long accountId = Long.parseLong(getTokenAt(0));
          place.setAccountId(accountId);
          boolean showTransaction = Boolean.parseBoolean(getTokenAt(1));
          place.setShowTransactions(showTransaction);
          return place;
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
