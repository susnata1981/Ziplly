package com.ziplly.app.shared;

import com.ziplly.app.model.EntityType;

import net.customware.gwt.dispatch.shared.Action;

public class GetEntityListAction implements Action<GetEntityResult> {
	private int page;
	private int pageSize;
	private EntityType entityType;
	private boolean needTotalEntityCount;
	
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
}