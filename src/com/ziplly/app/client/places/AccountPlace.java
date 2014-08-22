package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;

public class AccountPlace extends Place {
  public static final String TRANSACTION_VIEW_TOKEN = "showTransaction";

  private long accountId;
	private boolean showTransactions;

	public AccountPlace(long accountId) {
	  this.accountId = accountId;
  }
	
	public boolean isShowTransactions() {
	  return showTransactions;
  }

	public void setShowTransactions(boolean showTransactions) {
	  this.showTransactions = showTransactions;
  }

  public long getAccountId() {
    return accountId;
  }

  public void setAccountId(long accountId) {
    this.accountId = accountId;
  }
}
