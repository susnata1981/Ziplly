package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.ziplly.app.client.view.StringConstants;

public class ResidentPlace extends Place {
	private String token;
	private Long accountId;
	private Long neighborhoodId;
	
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

	@Prefix("residents")
	public static class Tokenizer implements PlaceTokenizer<ResidentPlace> {

		@Override
		public ResidentPlace getPlace(String token) {
			if (token != null) {
				String[] tokens = token.split(StringConstants.PLACE_SEPARATOR);
				if (tokens.length > 1) {
					if (tokens[0].equalsIgnoreCase(StringConstants.SEND_MESSAGE_TOKEN)) {
						try {
							long accountId = Long.parseLong(tokens[1]);
							ResidentPlace place = new ResidentPlace();
							place.setAccountId(accountId);
							return place;
						} catch (NumberFormatException nfe) {
							return new ResidentPlace("");
						}
					}
					else if (tokens[0].equalsIgnoreCase(StringConstants.NEIGHBORHOOD_TOKEN)) {
						try {
							long neighborhoodId = Long.parseLong(tokens[1]);
							ResidentPlace place = new ResidentPlace();
							place.setNeighborhoodId(neighborhoodId);
							return place;
						} catch (NumberFormatException nfe) {
							return new ResidentPlace("");
						}
					}
				}
				return new ResidentPlace(token);
			}
			return new ResidentPlace("");
		}

		@Override
		public String getToken(ResidentPlace place) {
			if (place.getAccountId() != null) {
				return PlaceUtils.getPlaceTokenForMessaging(place.getAccountId());
			}
			else if (place.getNeighborhoodId() != null) {
				return PlaceUtils.getPlaceTokenForNeighborhood(place.getNeighborhoodId());
			} else {
				return "";
			}
		}
	}
}
