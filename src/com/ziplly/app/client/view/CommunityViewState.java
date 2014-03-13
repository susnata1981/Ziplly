package com.ziplly.app.client.view;

import com.google.gwt.view.client.Range;
import com.ziplly.app.model.EntityType;
import com.ziplly.app.shared.GetEntityListAction;
import com.ziplly.app.shared.GetEntityListAction.SearchType;

public class CommunityViewState {
	GetEntityListAction action = new GetEntityListAction();
	protected int start;
	protected int pageSize;
	protected Long neighborhoodId;

	public CommunityViewState(EntityType type, int pageSize) {
		start = 0;
		this.pageSize = pageSize;
		action.setEntityType(type);
		action.setPageSize(this.pageSize);
	}

	public void searchByZip(String zip) {
		reset();
		action.setSearchType(SearchType.BY_ZIP);
		action.setZip(zip);
		action.setPage(start);
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
		action.setNeedTotalEntityCount(true);
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

	public void setNeighborhood(Long neighborhoodId) {
		this.neighborhoodId = neighborhoodId;
	}

	public Long getNeighborhoodId() {
		return neighborhoodId;
	}
}
