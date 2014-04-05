package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.model.Gender;

public class ResidentPlace extends Place {
	private String token;
	private Long accountId;
	private Long neighborhoodId;
	private Gender gender;

	public ResidentPlace() {
		this.setToken("");
	}

	public ResidentPlace(String token) {
		this.setToken(token);
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

	public Long getNeighborhoodId() {
		return neighborhoodId;
	}

	public void setNeighborhoodId(Long neighborhoodId) {
		this.neighborhoodId = neighborhoodId;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	@Prefix("residents")
	public static class Tokenizer implements PlaceTokenizer<ResidentPlace> {

		@Override
		public ResidentPlace getPlace(String token) {
			if (token != null) {
				try {
					String[] tokens = token.split(StringConstants.PLACE_SEPARATOR);
					if (tokens.length > 1) {
						ResidentPlace place = new ResidentPlace();
						for (int i = 0; i < tokens.length;i++) {
							String tok = tokens[i];
							String[] split = tok.split(StringConstants.PLACE_VALUE_SEPARATOR);
							if (split[0].equalsIgnoreCase(StringConstants.SEND_MESSAGE_TOKEN)) {
								long accountId = Long.parseLong(split[1]);
								place.setAccountId(accountId);
							} else if (split[0].equalsIgnoreCase(StringConstants.NEIGHBORHOOD_TOKEN)) {
								long neighborhoodId = Long.parseLong(split[1]);
								place.setNeighborhoodId(neighborhoodId);
							} else if (split[0].equalsIgnoreCase(StringConstants.GENDER_TOKEN)) {
								String gender = split[1];
								place.setGender(Gender.valueOf(gender));
							} else {
								throw new RuntimeException("Invalid url in resident view");
							}
						}
						return place;
					}
				} catch (Exception ex) {
					// Something happened.
					return new ResidentPlace(token);
				}
			}
			return new ResidentPlace("");
		}

		@Override
		public String getToken(ResidentPlace place) {
			if (place.getAccountId() != null) {
				return PlaceUtils.getPlaceTokenForMessaging(place.getAccountId(), place.getNeighborhoodId(), place.getGender());
			} else if (place.getNeighborhoodId() != null) {
				return PlaceUtils.getPlaceTokenForNeighborhood(place.getNeighborhoodId());
			} else {
				return "";
			}
		}
	}
}
