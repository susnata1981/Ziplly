package com.ziplly.app.shared;

import com.ziplly.app.model.NeighborhoodDTO;

import net.customware.gwt.dispatch.shared.Result;

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
