package com.ziplly.app.shared;

import com.ziplly.app.model.NeighborhoodDTO;

import net.customware.gwt.dispatch.shared.Action;

public class CreateNeighborhoodAction implements Action<CreateNeighborhoodResult> {
	private NeighborhoodDTO neighborhood;

	public CreateNeighborhoodAction() {
	}
	
	public CreateNeighborhoodAction(NeighborhoodDTO n) {
		this.neighborhood = n;
	}

	public NeighborhoodDTO getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(NeighborhoodDTO neighborhood) {
		this.neighborhood = neighborhood;
	}
}
