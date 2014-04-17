package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.model.TweetType;

public class HomePlace extends Place {
	private Long tweetId;
	private String tweetType;

	public HomePlace() {
		this(TweetType.ALL);
	}

	public HomePlace(TweetType type) {
		this.tweetType = type.name().toLowerCase();
	}

	public HomePlace(Long tweetId) {
		this.tweetId = tweetId;
	}

	public String getTweetType() {
		return tweetType;
	}

	public void setTweetType(String tweetType) {
		this.tweetType = tweetType;
	}

	public Long getTweetId() {
		return tweetId;
	}

	public void setTweetId(Long tweetId) {
		this.tweetId = tweetId;
	}

	@Prefix("home")
	public static class Tokenizer implements PlaceTokenizer<HomePlace> {
		@Override
		public HomePlace getPlace(String token) {
			if (token != null) {
				try {
					String[] tokens = token.split(StringConstants.PLACE_SEPARATOR);
					if (tokens.length > 1) {
						if (tokens[0].startsWith(StringConstants.HOME_TWEET_TOKEN)) {
							try {
								long tweetId = Long.parseLong(tokens[1]);
								HomePlace place = new HomePlace();
								place.setTweetId(tweetId);
								return place;
							} catch (NumberFormatException ex) {
								return new HomePlace();
							}
						} else {
							TweetType type = TweetType.valueOf(tokens[1].toUpperCase());
							return new HomePlace(type);
						}
					}
				} catch (IllegalArgumentException ex) {
					return new HomePlace();
				}
			}
			return new HomePlace();
		}

		@Override
		public String getToken(HomePlace place) {
			if (place.getTweetId() != null) {
				return PlaceUtils.getHomePlaceTokenForMessaging(place.getTweetId());
			} else if (place.getTweetType() != null) {
				return PlaceUtils.getHomePlaceTokenForTweetType(place.getTweetType());
			} else {
				return "";
			}
		}
	}
}
