package com.ziplly.app.shared;

import com.ziplly.app.model.EntityType;

import net.customware.gwt.dispatch.shared.Action;

public class GetEntityListAction implements Action<GetEntityResult> {
	
	private EntityType entityType;
	
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
}