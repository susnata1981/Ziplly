package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class HomePlace extends Place {
	private String filter;
	
	public HomePlace() {
		setFilter("all");
	}
	
	public HomePlace(String filter) {
		if (filter != null) {
			this.setFilter(filter);
		} else {
			setFilter("all");
		}
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
			return "all";
		}
	}
}
