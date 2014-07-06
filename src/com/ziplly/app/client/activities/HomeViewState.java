package com.ziplly.app.client.activities;

import java.util.List;

import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.shared.GetCommunityWallDataAction;
import com.ziplly.app.shared.GetCommunityWallDataAction.SearchType;

public class HomeViewState {
  private NeighborhoodDTO currentNeighborhood;
  private List<TweetDTO> currentTweetList;
  private boolean fetchingData = false;
  private GetCommunityWallDataAction lastSearchAction;
  private int page = 0;
  private int pageSize = 5;

  public NeighborhoodDTO getCurrentNeighborhood() {
    return currentNeighborhood;
  }

  public List<TweetDTO> getCurrentTweetList() {
    return currentTweetList;
  }

  public GetCommunityWallDataAction getLastSearchAction() {
    return lastSearchAction;
  }

  public GetCommunityWallDataAction getNextSearchAction() {
    lastSearchAction.setPage(++page);
    return lastSearchAction;
  }

  /**
   * Returns search criteria object based on place
   * 
   * @param place
   * @return
   */
  public GetCommunityWallDataAction getSearchCriteria(HomePlace place) {
    return parsePlaceToken(place);
  }

  public GetCommunityWallDataAction getSearchCriteria(NeighborhoodDTO neighborhood) {
    this.setCurrentNeighborhood(neighborhood);
    GetCommunityWallDataAction action = getCommunityWallDataAction();
    action.setSearchType(SearchType.NEIGHBORHOOD);
    action.setNeighborhood(neighborhood);
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

  public GetCommunityWallDataAction getSearchCriteriaForTweetType(TweetType type) {
    GetCommunityWallDataAction action = getCommunityWallDataAction();
    action.setSearchType(SearchType.CATEGORY);
    action.setType(type);
    action.setNeighborhood(currentNeighborhood);

    resetLastSearchAction(action);
    return action;
  }

  public boolean hasMorePages() {
    if (isFetchingData()) {
      return true;
    }
    return currentTweetList != null && currentTweetList.size() == pageSize;
  }

  public boolean isFetchingData() {
    return fetchingData;
  }

  public void setCurrentNeighborhood(NeighborhoodDTO currentNeighborhood) {
    this.currentNeighborhood = currentNeighborhood;
  }

  public void setCurrentTweetList(List<TweetDTO> currentTweetList) {
    this.currentTweetList = currentTweetList;
  }

  public void setFetchingData(boolean fetchingData) {
    this.fetchingData = fetchingData;
  }

  public void setLastSearchAction(GetCommunityWallDataAction lastSearchAction) {
    this.lastSearchAction = lastSearchAction;
  }

  private GetCommunityWallDataAction getCommunityWallDataAction() {
    GetCommunityWallDataAction action = new GetCommunityWallDataAction();
    action.setPageSize(pageSize);
    action.setPage(0);
    return action;
  }

  private GetCommunityWallDataAction getDefaultSearchAction() {
    resetLastSearchAction(new GetCommunityWallDataAction(TweetType.ALL, 0, pageSize));
    return getLastSearchAction();
  }

  private GetCommunityWallDataAction parsePlaceToken(HomePlace place) {
    GetCommunityWallDataAction action = getCommunityWallDataAction();

    if (place.getTweetId() != 0) {
      action.setTweetId(place.getTweetId());
      action.setSearchType(SearchType.TWEET_BY_ID);
    } else {
      try {
        TweetType type = TweetType.valueOf(place.getTweetType().toUpperCase());
        action.setSearchType(SearchType.CATEGORY);
        action.setNeighborhood(currentNeighborhood);
        action.setType(type);
      } catch (IllegalArgumentException ex) {
        return getDefaultSearchAction();
      }
    }

    resetLastSearchAction(action);
    return getLastSearchAction();
  }

  /**
   * Resets tweet search criteria
   * 
   * @param action
   */
  private void resetLastSearchAction(GetCommunityWallDataAction action) {
    page = 0;
    if (currentTweetList != null) {
      currentTweetList.clear();
    }

    setLastSearchAction(action);
  }
}
