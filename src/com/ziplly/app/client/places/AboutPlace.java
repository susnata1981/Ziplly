package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.ziplly.app.client.view.AboutViewSection;

public class AboutPlace extends Place {

	private AboutViewSection section;
	
	public AboutPlace() {
		this.section = AboutViewSection.ABOUTUS;
	}
	
	public AboutPlace(AboutViewSection section) {
		this.section = section;
	}

	public String getSection() {
		return section.name().toLowerCase();
	}

	public void setSection(String section) {
		this.section = AboutViewSection.valueOf(section);
	}

	@Prefix("about")
	public static class Tokenizer implements PlaceTokenizer<AboutPlace> {
		@Override
		public AboutPlace getPlace(String token) {
			try {
			AboutViewSection section = AboutViewSection.valueOf(token.toUpperCase());
			return new AboutPlace(section);
			} catch(RuntimeException ex) {
				return new AboutPlace(AboutViewSection.ABOUTUS);
			}
		}

		@Override
		public String getToken(AboutPlace place) {
			return place.getSection();
		}
	}
}
