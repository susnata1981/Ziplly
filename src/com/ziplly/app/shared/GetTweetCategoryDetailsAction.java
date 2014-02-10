package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class GetTweetCategoryDetailsAction implements Action<GetTweetCategoryDetailsResult>{

	private Long neighborhoodId;
	public GetTweetCategoryDetailsAction() {
	}
	
	public GetTweetCategoryDetailsAction(Long neighborhoodId) {
		this.neighborhoodId = neighborhoodId;
	}
	
	public Long getNeighborhoodId() {
		return neighborhoodId;
	}
	public void setNeighborhoodId(Long neighborhoodId) {
		this.neighborhoodId = neighborhoodId;
	}
}
