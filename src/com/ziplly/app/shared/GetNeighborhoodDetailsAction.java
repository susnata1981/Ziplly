package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class GetNeighborhoodDetailsAction implements Action<GetNeighborhoodDetailsResult> {

	private Long neighborhoodId;

	public GetNeighborhoodDetailsAction() {
	}

	public GetNeighborhoodDetailsAction(Long neighborhoodId) {
		this.setNeighborhoodId(neighborhoodId);
	}

	public Long getNeighborhoodId() {
		return neighborhoodId;
	}

	public void setNeighborhoodId(Long neighborhoodId) {
		this.neighborhoodId = neighborhoodId;
	}
}
