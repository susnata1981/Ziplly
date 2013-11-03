package com.ziplly.app.shared;

import java.util.List;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.PersonalAccountDTO;


public class GetResidentsResult implements Result {
	private List<PersonalAccountDTO> accounts;

	public GetResidentsResult(List<PersonalAccountDTO> accounts) {
		this.setAccounts(accounts);
	}

	public GetResidentsResult() {
	}

	public List<PersonalAccountDTO> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<PersonalAccountDTO> accounts) {
		this.accounts = accounts;
	}
}
