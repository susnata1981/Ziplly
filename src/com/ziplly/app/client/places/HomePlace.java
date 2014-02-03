package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.ziplly.app.model.TweetType;

public class HomePlace extends Place {
	private String filter;
	
	public HomePlace() {
		setFilter(TweetType.ALL.name().toLowerCase());
	}
	
	public HomePlace(TweetType type) {
		setFilter(type.name().toLowerCase());
	}
	
	public HomePlace(Long tweetId) {
		setFilter("tweet:"+tweetId);
	}
	
	protected HomePlace(String filter) {
		setFilter(filter);
	}
	
	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	@Prefix("home")
	public static class Tokenizer implements PlaceTokenizer<HomePlace> {
		@Override
		public HomePlace getPlace(String token) {
			if (token != null) {
				return new HomePlace(token);
			}
			return new HomePlace();
		}

		@Override
		public String getToken(HomePlace place) {
			if (place.getFilter() != null) {
				return place.getFilter();
			}
			return TweetType.ALL.name();
		}
	}
}
