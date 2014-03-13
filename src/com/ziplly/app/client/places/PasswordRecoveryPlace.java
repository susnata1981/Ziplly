package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class PasswordRecoveryPlace extends Place {
	private String hash;

	public PasswordRecoveryPlace() {
	}

	public PasswordRecoveryPlace(String hash) {
		this.setHash(hash);
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	@Prefix("passwordrecovery")
	public static class Tokenizer implements PlaceTokenizer<PasswordRecoveryPlace> {

		@Override
		public PasswordRecoveryPlace getPlace(String hash) {
			if (hash != null && !hash.equals("")) {
				return new PasswordRecoveryPlace(hash);
			}

			return new PasswordRecoveryPlace();
		}

		@Override
		public String getToken(PasswordRecoveryPlace place) {
			if (place.getHash() != null) {
				return place.getHash();
			}
			return "";
		}
	}
}
