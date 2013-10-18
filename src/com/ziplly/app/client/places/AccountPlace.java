package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class AccountPlace extends Place{
	private Long accountId;

	public AccountPlace() {
	}
	
	public AccountPlace(Long id) {
		this.setAccountId(id);
	}
	
	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	@Prefix("account")
	public static class Tokenizer implements PlaceTokenizer<AccountPlace> {
		@Override
		public AccountPlace getPlace(String token) {
			if (token != null && !"".equals(token)) {
				return new AccountPlace(Long.parseLong(token));
			}
			return new AccountPlace();
		}

		@Override
		public String getToken(AccountPlace place) {
			if (place.getAccountId() != null) {
				return place.getAccountId().toString();
			}
			return "";
		}
	}
}
