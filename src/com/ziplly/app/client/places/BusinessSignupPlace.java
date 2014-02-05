package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class BusinessSignupPlace extends Place {
	private String code;
	
	public BusinessSignupPlace() {
		this.code = "";
	}
	
	public BusinessSignupPlace(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Prefix("businesssignup")
	public static class Tokenizer implements PlaceTokenizer<BusinessSignupPlace> {
		@Override
		public BusinessSignupPlace getPlace(String token) {
			if (token != null) {
				return new BusinessSignupPlace(token);
			}
			return new BusinessSignupPlace("");
		}

		@Override
		public String getToken(BusinessSignupPlace place) {
			return "";
		}
	}
}
