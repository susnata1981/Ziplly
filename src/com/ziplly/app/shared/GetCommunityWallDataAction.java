package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.TweetType;

public class GetCommunityWallDataAction implements Action<GetCommunityWallDataResult> {
	public static enum SearchType {
		CATEGORY, HASHTAG, TWEET_BY_ID, NEIGHBORHOOD;
	}

	private GetCommunityWallDataAction.SearchType searchType;
	private TweetType type;
	private String hashtag;
	private String tweetId;
	private int page;
	private int pageSize;
	private NeighborhoodDTO neighborhood;

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

	public String getTweetId() {
		return tweetId;
	}

	public void setTweetId(String tweetId) {
		this.tweetId = tweetId;
	}

	public void setNeighborhood(NeighborhoodDTO neighborhood) {
		this.neighborhood = neighborhood;
	}

	public NeighborhoodDTO getNeighborhood() {
		return neighborhood;
	}
}
