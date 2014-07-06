package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.Prefix;
import com.google.inject.Inject;

public class CouponReportPlace extends Place {
	
	@Prefix("couponreport")
	public static class Tokenizer extends BaseTokenizer<CouponReportPlace> {

    @Override
    public CouponReportPlace getPlace(String token) {
			return new CouponReportPlace();
    }

		@Override
    public String getToken(CouponReportPlace place) {
			return "";
    }
	}
}
