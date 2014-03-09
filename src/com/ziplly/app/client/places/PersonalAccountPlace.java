package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class PersonalAccountPlace extends Place {
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
	public static class Tokenizer implements PlaceTokenizer<PersonalAccountPlace> {
		@Override
		public PersonalAccountPlace getPlace(String token) {
			if (token != null && "".equals(token)) {
				return new PersonalAccountPlace();
			}
			return new PersonalAccountPlace(Long.parseLong(token));
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
