package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Result;

public class GetNeighborhoodDetailsResult implements Result {
	private int totalResidents;
	private int totalBusinesses;
	
	public GetNeighborhoodDetailsResult() {
	}
	
	public int getTotalResidents() {
		return totalResidents;
	}

	public void setTotalResidents(int totalResidents) {
		this.totalResidents = totalResidents;
	}

	public int getTotalBusinesses() {
		return totalBusinesses;
	}

	public void setTotalBusinesses(int totalBusinesses) {
		this.totalBusinesses = totalBusinesses;
	}
}
