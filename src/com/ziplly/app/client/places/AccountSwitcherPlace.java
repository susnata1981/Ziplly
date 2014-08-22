package com.ziplly.app.client.places;

import java.util.Map;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.Prefix;

public class AccountSwitcherPlace extends Place { 
  public static final String TOKEN = "accountswitch";
  
  private long accountId;
  
  public AccountSwitcherPlace() {
    this.accountId = 0L;
  }
  
  public AccountSwitcherPlace(long accountId) {
    this.setAccountId(accountId);
  }
  
  public long getAccountId() {
    return accountId;
  }

  public void setAccountId(long accountId) {
    this.accountId = accountId;
  }

  @Prefix("accountswitch")
  public static class Tokenizer extends BaseTokenizer<AccountSwitcherPlace> {
    
    @Override
    public AccountSwitcherPlace getPlace(String token) {
      try {
        AccountSwitcherPlace place = new AccountSwitcherPlace(0L);
        Map<AttributeKey, AttributeValue> params = parser.parse(token);
        
        for(AttributeKey key: params.keySet()) {
          if (key.equals(AttributeKey.ACCOUNT_ID)) {
            place.setAccountId(Long.parseLong(params.get(key).value()));
          }
        }
        return place;
      } catch (Exception ex) {
        return new AccountSwitcherPlace(0L);
      }
    }

    @Override
    public String getToken(AccountSwitcherPlace place) {
      return PlaceUtils.getPlaceToken(place);
    }
  }
}
