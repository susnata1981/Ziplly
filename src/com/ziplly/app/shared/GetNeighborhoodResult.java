package com.ziplly.app.shared;

import java.util.List;

import com.ziplly.app.model.NeighborhoodDTO;

import net.customware.gwt.dispatch.shared.Result;

public class GetNeighborhoodResult implements Result {
	private List<NeighborhoodDTO> neighbordhoods;

	public GetNeighborhoodResult() {
	}
	
	public GetNeighborhoodResult(List<NeighborhoodDTO> neighborhoods) {
		this.neighbordhoods = neighborhoods;
	}
	
	public List<NeighborhoodDTO> getNeighbordhoods() {
		return neighbordhoods;
	}

	public void setNeighbordhoods(List<NeighborhoodDTO> neighbordhoods) {
		this.neighbordhoods = neighbordhoods;
	}
}
