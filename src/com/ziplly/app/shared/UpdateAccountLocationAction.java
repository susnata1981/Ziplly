package com.ziplly.app.shared;

import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.LocationDTO;

import net.customware.gwt.dispatch.shared.Action;

public class UpdateAccountLocationAction implements Action<UpdateAccountLocationResult> {
	private AccountDTO account;
	private LocationDTO location;

	public UpdateAccountLocationAction() {
  }
	
	public UpdateAccountLocationAction(AccountDTO acct, LocationDTO newLocation) {
		this.setAccount(acct);
		this.setLocation(newLocation);
  }

	public AccountDTO getAccount() {
	  return account;
  }

	public void setAccount(AccountDTO account) {
	  this.account = account;
  }

	public LocationDTO getLocation() {
	  return location;
  }

	public void setLocation(LocationDTO location) {
	  this.location = location;
  }
}
