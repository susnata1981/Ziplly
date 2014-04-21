package com.ziplly.app.client.places;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.ziplly.app.client.view.StringConstants;

public abstract class BaseTokenizer<T extends Place> implements PlaceTokenizer<T> {
	private final List<String> tokens = new ArrayList<String>();
	
	public void tokenize(String token) {
		if (token != null) {
			try {
				tokens.clear();
				tokens.addAll(Arrays.asList(token.split(StringConstants.PLACE_SEPARATOR)));
			} catch (IllegalArgumentException ex) {
				throw new IllegalArgumentException("Invalid token");
			}
		}
	}

	public List<String> getTokens() {
	  return tokens;
  }
	
	public String getTokenAt(int index) {
		if (tokens.size() > 0 && index < tokens.size()) {
			return tokens.get(index);
		}
		
		throw new IllegalArgumentException("Invalid range");
	}
}
