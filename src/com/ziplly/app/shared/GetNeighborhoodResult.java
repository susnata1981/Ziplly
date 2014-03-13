package com.ziplly.app.shared;

import java.util.ArrayList;
import java.util.List;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.NeighborhoodDTO;

public class GetNeighborhoodResult implements Result {
	private ArrayList<NeighborhoodDTO> neighbordhoods = new ArrayList<NeighborhoodDTO>();

	public GetNeighborhoodResult() {
	}

	public GetNeighborhoodResult(ArrayList<NeighborhoodDTO> neighborhoods) {
		this.neighbordhoods = neighborhoods;
	}

	public List<NeighborhoodDTO> getNeighbordhoods() {
		return neighbordhoods;
	}

	public void setNeighbordhoods(ArrayList<NeighborhoodDTO> neighbordhoods) {
		this.neighbordhoods = neighbordhoods;
	}
}
