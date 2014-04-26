package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.Prefix;

public class CouponDetailsPlace extends Place {
	
	@Prefix("coupon")
	public static class Tokenizer extends BaseTokenizer<CouponDetailsPlace> {

		private static final String REDEEM_TOKEN = "redeem";
		@Override
    public CouponDetailsPlace getPlace(String token) {
			try {
				tokenize(token);
				String token0 = getTokenAt(0);
				String token1 = getTokenAt(1);
				
				if (REDEEM_TOKEN.equalsIgnoreCase(token0)) {
					
				}
			} catch(Exception ex) {
				
			}
    }

		@Override
    public String getToken(CouponDetailsPlace place) {
	    // TODO Auto-generated method stub
	    return null;
    }
	}
}
