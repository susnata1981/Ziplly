package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.ziplly.app.client.view.StringConstants;

public class EmailVerificationPlace extends Place {

	private String code;
	private String id;
	
	public EmailVerificationPlace(String code, String id) {
		this.code = code;
		this.id = id;
	}
	
	public EmailVerificationPlace() {
	}
	
	@Prefix("emailverification")
	public static class Tokenizer implements PlaceTokenizer<EmailVerificationPlace> {
		@Override
		public EmailVerificationPlace getPlace(String token) {
			String[] tokens = token.split(StringConstants.URL_PARAMATER_SEPARATOR);
			if (tokens.length == 2) {
				EmailVerificationPlace place = new EmailVerificationPlace();
				place.setCode(tokens[0].trim());
				place.setId(tokens[1].trim());
				return place;
			}
			return new EmailVerificationPlace();
		}

		@Override
		public String getToken(EmailVerificationPlace place) {
			return "";
		}
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
