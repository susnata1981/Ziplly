package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
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
	public static class Tokenizer extends BaseTokenizer<HomePlace> {
	
		@Override
		public HomePlace getPlace(String token) {
			try {
				tokenize(token);
				String token0 = getTokenAt(0);
				if (token0.startsWith(StringConstants.HOME_TWEET_TOKEN)) {
					long tweetId = Long.parseLong(getTokenAt(1));
					HomePlace place = new HomePlace();
					place.setTweetId(tweetId);
					return place;
				} else {
					TweetType type = TweetType.valueOf(getTokenAt(1).toUpperCase());
					return new HomePlace(type);
				}
			} catch (Exception ex) {
				// return default HomePlace.
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
