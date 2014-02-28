package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class SwitchLocationAction implements Action<SwitchLocationResult>{
	private Long locationId;
	
	public SwitchLocationAction() {
	}
	
	public SwitchLocationAction(Long locationId) {
		this.setLocationId(locationId);
	}

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}
}
