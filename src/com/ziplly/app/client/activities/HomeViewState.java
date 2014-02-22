package com.ziplly.app.client.activities;

import java.util.List;

import com.google.gwt.user.client.ui.RootPanel;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.shared.GetCommunityWallDataAction;
import com.ziplly.app.shared.GetCommunityWallDataAction.SearchType;

public class HomeViewState {
	private GetCommunityWallDataAction lastSearchAction;
	private List<TweetDTO> currentTweetList;
	private boolean fetchingData = false;
	private int page = 0;
	private int pageSize = 5;
	private NeighborhoodDTO currentNeighborhood;
	
	/**
	 * Returns search criteria object based on place
	 * @param place
	 * @return
	 */
	public GetCommunityWallDataAction getSearchCriteria(HomePlace place) {
		return parsePlaceToken(place);
	}

	private GetCommunityWallDataAction parsePlaceToken(HomePlace place) {
		GetCommunityWallDataAction action = getCommunityWallDataAction();
		String filter = place.getFilter();
		if (filter.startsWith("tweet")) {
			String[] tokens = filter.split(":");
			if (!validTweetId(tokens)) {
				return getDefaultSearchAction();
			}
			action.setSearchType(SearchType.TWEET_BY_ID);
			action.setTweetId(tokens[1]);
		} else {
			TweetType type;
			try {
				type = TweetType.valueOf(filter.toUpperCase());
			} catch (IllegalArgumentException ex) {
				return getDefaultSearchAction();
			}
			action.setNeighborhood(currentNeighborhood);
			action.setSearchType(SearchType.CATEGORY);
			action.setType(type);
		}
		
		resetLastSearchAction(action);
		return getLastSearchAction();
	}

	public GetCommunityWallDataAction getSearchCriteriaForTweetType(TweetType type) {
		GetCommunityWallDataAction action = getCommunityWallDataAction();
		action.setSearchType(SearchType.CATEGORY);
		action.setType(type);
		action.setNeighborhood(currentNeighborhood);
		
		resetLastSearchAction(action);
		return action;
	}
	
	public GetCommunityWallDataAction getSearchCriteriaForHashtag(String hashtag) {
		GetCommunityWallDataAction action = getCommunityWallDataAction();
		action.setSearchType(SearchType.HASHTAG);
		action.setHashtag(hashtag);
		
		resetLastSearchAction(action);
		return action;
	}

	private boolean validTweetId(String [] tokens) {
		if (tokens.length != 2) {
			return false;
		}
		try {
			Long.parseLong(tokens[1]);
		} catch(NumberFormatException nfe) {
			return false;
		}
		
		return true;
	}

	public GetCommunityWallDataAction getLastSearchAction() {
		return lastSearchAction;
	}

	public GetCommunityWallDataAction getNextSearchAction() {
		lastSearchAction.setPage(++page);
		return lastSearchAction;
	}
	
	public boolean hasMorePages() {
		if (isFetchingData()) {
			return true;
		}
		return currentTweetList != null && currentTweetList.size() == pageSize;
	}
	
	public void setLastSearchAction(GetCommunityWallDataAction lastSearchAction) {
		this.lastSearchAction = lastSearchAction;
	}

	public boolean isFetchingData() {
		return fetchingData;
	}

	public void setFetchingData(boolean fetchingData) {
		this.fetchingData = fetchingData;
	}

	public List<TweetDTO> getCurrentTweetList() {
		return currentTweetList;
	}

	public void setCurrentTweetList(List<TweetDTO> currentTweetList) {
		this.currentTweetList = currentTweetList;
	}
	
	private GetCommunityWallDataAction getDefaultSearchAction() {
		resetLastSearchAction(new GetCommunityWallDataAction(TweetType.ALL, 0, pageSize)); 
		return getLastSearchAction();
	}
	
	/**
	 * Resets tweet search criteria
	 * @param action
	 */
	private void resetLastSearchAction(GetCommunityWallDataAction action) {
		page = 0;
		if (currentTweetList != null) {
			currentTweetList.clear();
		}
		
		setLastSearchAction(action); 
	}
	
	private GetCommunityWallDataAction getCommunityWallDataAction() {
		GetCommunityWallDataAction action = new GetCommunityWallDataAction();
		action.setPageSize(pageSize);
		action.setPage(0);
		return action;
	}

	public GetCommunityWallDataAction getSearchCriteria(NeighborhoodDTO neighborhood) {
		this.setCurrentNeighborhood(neighborhood);
		GetCommunityWallDataAction action = getCommunityWallDataAction();
		action.setSearchType(SearchType.NEIGHBORHOOD);
		action.setNeighborhood(neighborhood);
		resetLastSearchAction(action);
		return action;
	}

	public NeighborhoodDTO getCurrentNeighborhood() {
		return currentNeighborhood;
	}

	public void setCurrentNeighborhood(NeighborhoodDTO currentNeighborhood) {
		this.currentNeighborhood = currentNeighborhood;
	}
}
