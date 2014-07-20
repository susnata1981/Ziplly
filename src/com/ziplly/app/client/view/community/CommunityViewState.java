package com.ziplly.app.client.view.community;

import com.google.gwt.view.client.Range;
import com.ziplly.app.model.EntityType;
import com.ziplly.app.shared.GetEntityListAction;
import com.ziplly.app.shared.GetEntityListAction.SearchType;

public class CommunityViewState {
  public static final int PAGE_SIZE = 10;
	protected GetEntityListAction action = new GetEntityListAction();
	protected int start;
	protected long neighborhoodId;

	public CommunityViewState(EntityType type, long neighborhoodId) {
		start = 0;
		this.neighborhoodId = neighborhoodId;
		action.setEntityType(type);
		action.setPageSize(PAGE_SIZE);
    action.setNeedTotalEntityCount(true);
	}

	public void searchByNeighborhood(Long neighborhoodId) {
		reset();
		this.neighborhoodId = neighborhoodId;
		action.setSearchType(SearchType.BY_NEIGHBORHOOD);
		action.setPage(start);
	}

	public void reset() {
		start = 0;
		action.setSearchType(SearchType.OTHER);
	}

	public GetEntityListAction getCurrentEntityListAction() {
		action.setPage(start);
		action.setNeighborhoodId(neighborhoodId);
		return action;
	}

	public void setRange(Range r) {
		start = r.getStart();
	}

	public int getStart() {
		return start;
	}

	public void setNeighborhood(long neighborhoodId) {
		this.neighborhoodId = neighborhoodId;
	}

	public long getNeighborhoodId() {
		return neighborhoodId;
	}

  public int getPageSize() {
    return PAGE_SIZE;
  }
}
