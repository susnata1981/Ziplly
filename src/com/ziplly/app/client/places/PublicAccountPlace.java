package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class PublicAccountPlace extends Place {
	private Long accountId;

	public PublicAccountPlace() {
	}
	
	public PublicAccountPlace(Long accountId) {
		this.setAccountId(accountId);
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	
	@Prefix("publicaccount")
	public static class Tokenizer implements PlaceTokenizer<PublicAccountPlace> {
		@Override
		public PublicAccountPlace getPlace(String token) {
			if (token != null && !"".equals(token)) {
				return new PublicAccountPlace();
			}
			return new PublicAccountPlace(Long.parseLong(token));
		}

		@Override
		public String getToken(PublicAccountPlace place) {
			return "";
		}
	}	
}
