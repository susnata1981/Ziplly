package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

import com.ziplly.app.model.TweetType;

public class GetCommunityWallDataAction implements Action<GetCommunityWallDataResult> {
	public static enum SearchType {
		CATEGORY,
		HASHTAG;
	}

	private GetCommunityWallDataAction.SearchType searchType;
	private TweetType type;
	private String hashtag;
	private int page;
	private int pageSize;

	public GetCommunityWallDataAction() {
	}
	
	public GetCommunityWallDataAction(TweetType type, int page, int pageSize) {
		this.type = type;
		this.setPage(page);
		this.setPageSize(pageSize);
		searchType = GetCommunityWallDataAction.SearchType.CATEGORY;
	}
	
	public GetCommunityWallDataAction(String hashtag, int page, int pageSize) {
		this.hashtag = hashtag;
		this.setPage(page);
		this.setPageSize(pageSize);
		searchType = GetCommunityWallDataAction.SearchType.HASHTAG;
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

	public GetCommunityWallDataAction.SearchType getSearchType() {
		return searchType;
	}

	public void setSearchType(GetCommunityWallDataAction.SearchType searchType) {
		this.searchType = searchType;
	}

	public String getHashtag() {
		return hashtag;
	}

	public void setHashtag(String hashtag) {
		this.hashtag = hashtag;
	}
}
