package com.ziplly.app.shared;

import com.ziplly.app.model.EntityType;

import net.customware.gwt.dispatch.shared.Action;

public class GetNewMemberAction implements Action<GetNewMemberResult> {
	private int daysLookback = 30;
	private Long neighborhoodId;
	private EntityType entityType = EntityType.PERSONAL_ACCOUNT;
	
	public GetNewMemberAction() {
  }
	
	public int getDaysLookback() {
	  return daysLookback;
  }

	public void setDaysLookback(int daysLookback) {
	  this.daysLookback = daysLookback;
  }

	public Long getNeighborhoodId() {
	  return neighborhoodId;
  }

	public void setNeighborhoodId(Long neighborhoodId) {
	  this.neighborhoodId = neighborhoodId;
  }

	public EntityType getEntityType() {
	  return entityType;
  }

	public void setEntityType(EntityType entityType) {
	  this.entityType = entityType;
  }
}
