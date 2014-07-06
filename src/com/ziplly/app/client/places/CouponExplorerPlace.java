package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.Prefix;
import com.google.inject.Inject;

public class CouponExplorerPlace extends Place {

  @Prefix("couponexplorer")
  public static class Tokenizer extends BaseTokenizer<CouponExplorerPlace> {

    @Override
    public CouponExplorerPlace getPlace(String token) {
      return new CouponExplorerPlace();
    }

    @Override
    public String getToken(CouponExplorerPlace place) {
      return "";
    }
  }
}
