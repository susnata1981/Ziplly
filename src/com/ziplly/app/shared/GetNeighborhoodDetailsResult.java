package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Result;

public class GetNeighborhoodDetailsResult implements Result {
	private int totalResidents;

	public GetNeighborhoodDetailsResult() {
	}
	
	public int getTotalResidents() {
		return totalResidents;
	}

	public void setTotalResidents(int totalResidents) {
		this.totalResidents = totalResidents;
	}
}
