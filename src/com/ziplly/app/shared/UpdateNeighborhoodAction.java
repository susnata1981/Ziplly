package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

import com.ziplly.app.model.NeighborhoodDTO;

public class UpdateNeighborhoodAction implements Action<UpdateNeighborhoodResult> {

	private NeighborhoodDTO neighborhood;

	public UpdateNeighborhoodAction() {
	}

	public UpdateNeighborhoodAction(NeighborhoodDTO n) {
		this.setNeighborhood(n);
	}

	public NeighborhoodDTO getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(NeighborhoodDTO neighborhood) {
		this.neighborhood = neighborhood;
	}
}
