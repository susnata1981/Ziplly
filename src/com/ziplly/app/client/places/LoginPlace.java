package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class LoginPlace extends Place {
	private Long accountId;

	public LoginPlace() {
	}
	
	public LoginPlace(Long id) {
		this.setAccountId(id);
	}
	
	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	@Prefix("login")
	public static class Tokenizer implements PlaceTokenizer<LoginPlace> {
		@Override
		public LoginPlace getPlace(String token) {
			if (token != null && !"".equals(token)) {
				return new LoginPlace(Long.parseLong(token));
			}
			return new LoginPlace();
		}

		@Override
		public String getToken(LoginPlace place) {
			if (place.getAccountId() != null) {
				return place.getAccountId().toString();
			}
			return "";
		}
	}
}
