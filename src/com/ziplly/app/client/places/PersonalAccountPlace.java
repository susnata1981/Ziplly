package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Prefix;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class PersonalAccountPlace extends AccountPlace {
  private static final String TRANSACTION_VIEW_TOKEN = "transactions";
  private Long accountId;

  public PersonalAccountPlace(Long accountId) {
    this.setAccountId(accountId);
  }

  public PersonalAccountPlace() {
  }

  public Long getAccountId() {
    return accountId;
  }

  public void setAccountId(Long accountId) {
    this.accountId = accountId;
  }

  @Prefix("personalaccount")
  public static class Tokenizer extends BaseTokenizer<PersonalAccountPlace> {

    @Override
    public PersonalAccountPlace getPlace(String token) {
      tokenize(token);
      PersonalAccountPlace place = new PersonalAccountPlace();
      if (TRANSACTION_VIEW_TOKEN.equalsIgnoreCase(getTokenAt(0))) {
        place.setShowTransactions(true);
      } else {
        ValidationResult result = FieldVerifier.validateInteger(getTokenAt(0), Integer.MAX_VALUE);
        if (result.isValid()) {
          place.setAccountId(Long.parseLong(getTokenAt(0)));
          return place;
        }
      }
      return place;
    }

    @Override
    public String getToken(PersonalAccountPlace place) {
      if (place.getAccountId() != null) {
        return place.getAccountId().toString();
      }
      return "";
    }
  }

}
