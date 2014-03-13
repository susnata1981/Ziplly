package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class DeleteNeighborhoodAction implements Action<DeleteNeighborhoodResult> {
	private Long neighborhoodId;

	public DeleteNeighborhoodAction() {
	}

	public DeleteNeighborhoodAction(long neighborhoodId) {
		this.neighborhoodId = neighborhoodId;
	}

	public Long getNeighborhoodId() {
		return neighborhoodId;
	}

	public void setNeighborhoodId(Long neighborhoodId) {
		this.neighborhoodId = neighborhoodId;
	}
}
