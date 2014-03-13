package com.ziplly.app.shared;

import java.util.ArrayList;
import java.util.List;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.InterestDTO;

public class GetInterestResult implements Result {
	private List<InterestDTO> interests = new ArrayList<InterestDTO>();

	public GetInterestResult() {
	}

	public List<InterestDTO> getInterests() {
		return interests;
	}

	public void setInterests(List<InterestDTO> interests) {
		this.interests = interests;
	}
}
