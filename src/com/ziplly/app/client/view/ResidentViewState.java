package com.ziplly.app.client.view;

import java.util.List;

import com.ziplly.app.model.EntityType;
import com.ziplly.app.model.Gender;
import com.ziplly.app.shared.GetEntityListAction.SearchType;

public class ResidentViewState extends CommunityViewState {

	private Gender gender;
	public ResidentViewState(EntityType type, int pageSize) {
		super(type, pageSize);
		action.setSearchType(SearchType.BY_GENDER);
		setGender(Gender.ALL);
	}

	@Override
	public void reset() {
		start = 0;
		gender = Gender.ALL;
		action.setGender(gender);
		action.setNeedTotalEntityCount(true);
	}

	public void searchByGender(Gender gender) {
		reset();
		this.gender = gender;
		action.setGender(gender);
		action.setPage(start);
	}
	
	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}
}
