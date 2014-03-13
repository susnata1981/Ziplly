package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class GetTweetForUserAction implements Action<GetTweetForUserResult> {
	private long accountId;
	private int page;
	private int pageSize;

	public GetTweetForUserAction() {
	}

	public GetTweetForUserAction(Long accountId, int tweetPageIndex, int pageSize) {
		this.setAccountId(accountId);
		this.setPage(tweetPageIndex);
		this.setPageSize(pageSize);
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}
}
