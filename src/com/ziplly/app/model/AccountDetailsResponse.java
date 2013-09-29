package com.ziplly.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Window;

public class AccountDetailsResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	public List<AccountDetails> accounts = new ArrayList<AccountDetails>();
	public int resultSize;
	
	public void addAccountDetail(AccountDetails ad) {
		if (ad == null) {
			Window.alert("Trying to add null AccountDetails");
			return;
		}
		this.accounts.add(ad);
	}
}
