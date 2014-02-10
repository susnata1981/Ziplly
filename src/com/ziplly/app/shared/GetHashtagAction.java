package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class GetHashtagAction implements Action<GetHashtagResult>{
	private int size;
	private Long neighborhoodId;
	
	public GetHashtagAction() {
	}
	
	public GetHashtagAction(Long neighborhoodId) {
		this.neighborhoodId = neighborhoodId;
	}
	
	public GetHashtagAction(int n) {
		this.setSize(n);
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Long getNeighborhoodId() {
		return neighborhoodId;
	}

	public void setNeighborhoodId(Long neighborhoodId) {
		this.neighborhoodId = neighborhoodId;
	}
}
