package com.ziplly.app.shared;

import com.ziplly.app.model.AccountSearchCriteria;

import net.customware.gwt.dispatch.shared.Action;

public class SearchAccountAction implements Action<SearchAccountResult>{
	private AccountSearchCriteria criteria;
	
	public SearchAccountAction() {
	}
	
	public SearchAccountAction(AccountSearchCriteria criteria) {
		this.setCriteria(criteria);
	}

	public AccountSearchCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(AccountSearchCriteria criteria) {
		this.criteria = criteria;
	}
}
