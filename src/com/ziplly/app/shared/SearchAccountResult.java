package com.ziplly.app.shared;

import java.util.ArrayList;
import java.util.List;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.AccountDTO;

public class SearchAccountResult implements Result {
	private List<AccountDTO> accounts = new ArrayList<AccountDTO>();

	public SearchAccountResult() {
	}
	
	public List<AccountDTO> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<AccountDTO> accounts) {
		this.accounts = accounts;
	}
}
