package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;

public class AccountPlace extends Place {
	private boolean showTransactions;

	public boolean isShowTransactions() {
	  return showTransactions;
  }

	public void setShowTransactions(boolean showTransactions) {
	  this.showTransactions = showTransactions;
  }
}
