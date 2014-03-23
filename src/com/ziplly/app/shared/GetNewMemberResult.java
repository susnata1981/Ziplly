package com.ziplly.app.shared;

import java.util.List;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.AccountDTO;

public class GetNewMemberResult implements Result {
	private List<AccountDTO> accounts;

	public GetNewMemberResult() {
  }
	
	public GetNewMemberResult(List<AccountDTO> accounts) {
		this.setAccounts(accounts);
  }

	public List<AccountDTO> getAccounts() {
	  return accounts;
  }

	public void setAccounts(List<AccountDTO> accounts) {
	  this.accounts = accounts;
  }
}
