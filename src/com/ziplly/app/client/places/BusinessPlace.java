package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.ziplly.app.client.view.StringConstants;

public class BusinessPlace extends Place {
	private String token;
	private Long accountId;

	public BusinessPlace() {
		setToken("");
	}

	public BusinessPlace(String token) {
		setToken(token);
	}
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	@Prefix("business")
	public static class Tokenizer implements PlaceTokenizer<BusinessPlace> {
		@Override
		public BusinessPlace getPlace(String token) {
			if (token != null) {
				String[] tokens = token.split(StringConstants.PLACE_SEPARATOR);
				if (tokens.length > 1) {
					if (tokens[0].equalsIgnoreCase(StringConstants.SEND_MESSAGE_TOKEN)) {
						try {
							long accountId = Long.parseLong(tokens[1]);
							BusinessPlace place = new BusinessPlace();
							place.setAccountId(accountId);
							return place;
						} catch (NumberFormatException nfe) {
							return new BusinessPlace("");
						}
					}
				}
				return new BusinessPlace(token);
			}
			return new BusinessPlace("");
		}

		@Override
		public String getToken(BusinessPlace place) {
			if (place.getToken() != null || !place.getToken().equals("")) {
				return place.getToken();
			} else {
				return "";
			}
		}
	}
}
