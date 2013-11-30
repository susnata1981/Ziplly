package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

import com.ziplly.app.model.TweetSearchCriteria;

public class GetTweetsAction implements Action<GetTweetsResult> {
	private int start;
	private int end;
	private TweetSearchCriteria criteria;
	
	public GetTweetsAction() {
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

	public TweetSearchCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(TweetSearchCriteria criteria) {
		this.criteria = criteria;
	}
}
