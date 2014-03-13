package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.NeighborhoodDTO;

public class UpdateNeighborhoodResult implements Result {
	private NeighborhoodDTO neighborhood;

	public UpdateNeighborhoodResult() {
	}

	public UpdateNeighborhoodResult(NeighborhoodDTO n) {
		this.neighborhood = n;
	}

	public NeighborhoodDTO getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(NeighborhoodDTO neighborhood) {
		this.neighborhood = neighborhood;
	}
}
