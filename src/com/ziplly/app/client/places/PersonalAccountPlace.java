package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Prefix;

public class PersonalAccountPlace extends AccountPlace {
  public static final String TOKEN = "personalaccount";

  public PersonalAccountPlace(long accountId) {
    this.setAccountId(accountId);
  }

  public PersonalAccountPlace() {
  }

  @Prefix("personalaccount")
  public static class Tokenizer extends BaseTokenizer<PersonalAccountPlace> {

    @Override
    public PersonalAccountPlace getPlace(String token) {
      try {
        PersonalAccountPlace place = new PersonalAccountPlace();
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
        return new PersonalAccountPlace();
      }
    }

    @Override
    public String getToken(PersonalAccountPlace place) {
      return PlaceUtils.getPlaceToken(place);
    }
  }
}
