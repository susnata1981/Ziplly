package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class TweetDetailsPlace extends Place {
	private Long tweetId;
	
	@Prefix("tweet")
	public static class Tokenizer implements PlaceTokenizer<TweetDetailsPlace> {
		@Override
		public TweetDetailsPlace getPlace(String token) {
			long tweetId = Long.parseLong(token);
			TweetDetailsPlace place = new TweetDetailsPlace(tweetId);
			place.setTweetId(tweetId);
			return place;
		}

		@Override
		public String getToken(TweetDetailsPlace place) {
			if (place.getTweetId() != null) {
				return place.getTweetId().toString();
			}
			return "";
		}
	}

	public TweetDetailsPlace(Long tweetId) {
		this.tweetId = tweetId;
	}

	public Long getTweetId() {
		return tweetId;
	}

	public void setTweetId(Long tweetId) {
		this.tweetId = tweetId;
	}
}
