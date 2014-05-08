package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.Prefix;

public class PrintCouponPlace extends Place {
	private Long couponTransactionId;
	
	public PrintCouponPlace(Long transactionId) {
		try {
			setCouponTransactionId(transactionId);
		} catch(NumberFormatException nfe) {
			setCouponTransactionId(-1L);
		}
  }

	public Long getCouponTransactionId() {
	  return couponTransactionId;
  }

	public void setCouponTransactionId(Long couponTransactionId) {
	  this.couponTransactionId = couponTransactionId;
  }

	@Prefix("printcoupon")
	public static class Tokenizer extends BaseTokenizer<PrintCouponPlace> {

		@Override
    public PrintCouponPlace getPlace(String token) {
			return new PrintCouponPlace(Long.parseLong(token));
    }

		@Override
    public String getToken(PrintCouponPlace place) {
			return place.getCouponTransactionId().toString();
    }
	}
}
