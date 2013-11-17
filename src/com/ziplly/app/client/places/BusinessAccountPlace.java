package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class BusinessAccountPlace extends Place {
	private Long accountId;

	public BusinessAccountPlace() {
	}

	public BusinessAccountPlace(Long accountId) {
		this.setAccountId(accountId);
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	@Prefix("businessaccount")
	public static class Tokenizer implements PlaceTokenizer<BusinessAccountPlace> {
		@Override
		public BusinessAccountPlace getPlace(String token) {
			if (token != null && !"".equals(token)) {
				return new BusinessAccountPlace(Long.parseLong(token));
			}
			return new BusinessAccountPlace();
		}

		@Override
		public String getToken(BusinessAccountPlace place) {
			if (place.getAccountId() != null) {
				return place.getAccountId().toString();
			}
			return "";
		}
	}
}
