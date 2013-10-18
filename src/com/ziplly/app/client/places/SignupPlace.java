package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.ziplly.app.model.AccountDTO;

public class SignupPlace extends Place {
	private AccountDTO account;

	public SignupPlace() {
	}

	public SignupPlace(AccountDTO account) {
		this.setAccount(account);
	}

	public AccountDTO getAccount() {
		return account;
	}

	public void setAccount(AccountDTO account) {
		this.account = account;
	}
	
	@Prefix("signup")
	public static class Tokenizer implements PlaceTokenizer<SignupPlace> {
		@Override
		public SignupPlace getPlace(String token) {
			return new SignupPlace();
		}

		@Override
		public String getToken(SignupPlace place) {
			return "";
		}
	}
}
