package com.ziplly.app.client.places;

import java.util.List;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.Prefix;

public class PrintCouponPlace extends Place {
  private long orderId;
  private long couponId;

  public PrintCouponPlace(long orderId, long couponId) {
    try {
      setOrderId(orderId);
      this.setCouponId(couponId);
    } catch (NumberFormatException nfe) {
      setOrderId(-1L);
    }
  }

  public long getOrderId() {
    return orderId;
  }

  public void setOrderId(long orderId) {
    this.orderId = orderId;
  }

  public long getCouponId() {
    return couponId;
  }

  public void setCouponId(long couponId) {
    this.couponId = couponId;
  }

  @Prefix("printcoupon")
  public static class Tokenizer extends BaseTokenizer<PrintCouponPlace> {

    @Override
    public PrintCouponPlace getPlace(String token) {
      tokenize(token);
      try {
        List<String> tokens = getTokens();
        if (tokens.size() != 2) {
          PrintCouponPlace place = new PrintCouponPlace(-1, -1);
          return place;
        }
        long orderId = Long.parseLong(getTokenAt(0));
        long couponId = Long.parseLong(getTokenAt(1));
        PrintCouponPlace place = new PrintCouponPlace(orderId, couponId);
        return place;
      } catch (NumberFormatException ex) {
        PrintCouponPlace place = new PrintCouponPlace(-1, -1);
        return place;
      }
    }

    @Override
    public String getToken(PrintCouponPlace place) {
      return PlaceUtils.getPlaceToken(place);
    }
  }
}
