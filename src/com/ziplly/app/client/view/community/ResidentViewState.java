package com.ziplly.app.client.view.community;

import com.ziplly.app.model.EntityType;
import com.ziplly.app.model.Gender;
import com.ziplly.app.shared.GetEntityListAction.SearchType;

public class ResidentViewState extends CommunityViewState {
	private Gender gender;

	public ResidentViewState(EntityType type, long neighborhoodId, Gender gender) {
		super(type, neighborhoodId);
		this.gender = gender;
		
		action.setNeedTotalEntityCount(true);
	}

	@Override
	public void reset() {
		start = 0;
		gender = Gender.ALL;
		action.setGender(gender);
	}

	public void searchByGender(Gender gender) {
		reset();
		this.gender = gender;
		action.setPage(start);
		action.setGender(gender);
		action.setSearchType(SearchType.BY_GENDER);
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}
}
