package com.ziplly.app.model;

import java.io.Serializable;
import java.util.Date;

public class LocationDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long locationId;
	private String address;
	private String type;
	private NeighborhoodDTO neighborhood;
	private Date timeUpdated;
	private Date timeCreated;
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public NeighborhoodDTO getNeighborhood() {
		return neighborhood;
	}
	public void setNeighborhood(NeighborhoodDTO neighborhood) {
		this.neighborhood = neighborhood;
	}
	public Date getTimeUpdated() {
		return timeUpdated;
	}
	public void setTimeUpdated(Date timeUpdated) {
		this.timeUpdated = timeUpdated;
	}
	public Date getTimeCreated() {
		return timeCreated;
	}
	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}
	public Long getLocationId() {
		return locationId;
	}
	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		
		if (o == this) {
			return true;
		}
		
		if (!(o instanceof LocationDTO)) {
			return false;
		}
		
		LocationDTO l = (LocationDTO)o;
		return l.getLocationId() == locationId;
	}
	
	@Override
	public int hashCode() {
		return locationId.hashCode();
	}

	public LocationType getType() {
		return LocationType.valueOf(type);
	}
	
	public void setType(LocationType type) {
		this.type = type.name();
	}
}
