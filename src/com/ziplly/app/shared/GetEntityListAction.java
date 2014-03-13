package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

import com.ziplly.app.model.EntityType;
import com.ziplly.app.model.Gender;

public class GetEntityListAction implements Action<GetEntityResult> {
	public enum SearchType {
		BY_ZIP, BY_NEIGHBORHOOD, BY_GENDER, OTHER;
	}

	private int page;
	private int pageSize;
	private EntityType entityType;
	private boolean needTotalEntityCount;
	private SearchType searchType = SearchType.OTHER;
	private String zip;
	private Gender gender = Gender.ALL;
	private Long neighborhoodId;

	public GetEntityListAction() {
	}

	public GetEntityListAction(EntityType type) {
		this.entityType = type;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public boolean isNeedTotalEntityCount() {
		return needTotalEntityCount;
	}

	public void setNeedTotalEntityCount(boolean needTotalEntityCount) {
		this.needTotalEntityCount = needTotalEntityCount;
	}

	public SearchType getSearchType() {
		return searchType;
	}

	public void setSearchType(SearchType searchType) {
		this.searchType = searchType;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public void setNeighborhoodId(Long neighborhoodId) {
		this.neighborhoodId = neighborhoodId;
	}

	public Long getNeighborhoodId() {
		return neighborhoodId;
	}
}