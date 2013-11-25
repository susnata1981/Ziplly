package com.ziplly.app.shared;

import com.ziplly.app.model.TweetType;

import net.customware.gwt.dispatch.shared.Action;

public class GetCommunityWallDataAction implements Action<GetCommunityWallDataResult> {
	private TweetType type;
	private int page;
	private int pageSize;

	public GetCommunityWallDataAction() {
	}
	
	public GetCommunityWallDataAction(TweetType type, int page, int pageSize) {
		this.type = type;
		this.setPage(page);
		this.setPageSize(pageSize);
	}
	
	public TweetType getType() {
		return type;
	}
	
	public void setType(TweetType type) {
		this.type = type;
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
}
