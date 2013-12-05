package com.ziplly.app.shared;

import com.ziplly.app.model.AccountSearchCriteria;

import net.customware.gwt.dispatch.shared.Action;

public class SearchAccountAction implements Action<SearchAccountResult>{
	private AccountSearchCriteria criteria;
	private int start, end;
	
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
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
}
