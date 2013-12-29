package com.ziplly.app.model;

import java.io.Serializable;

public class NeighborhoodDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long neighborhoodId;
	private String name;
	private String city;
	private String state;
	private PostalCodeDTO postalCode;
	
	public NeighborhoodDTO() {
	}
	
	public Long getNeighborhoodId() {
		return neighborhoodId;
	}

	@Override
	public String toString() {
		return "(" + this.name + ", " + this.city + ", " + this.state + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		
		if (!(o instanceof Neighborhood)) {
			return false;
		}
		
		Neighborhood n = (Neighborhood)o;
		return n.getNeighborhoodId() == this.getNeighborhoodId();
	}

	public void setNeighborhoodId(Long neighborhoodId) {
		this.neighborhoodId = neighborhoodId;
	}

	// to be overridden
	public String getDisplayName() {
		return "<NAME>";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public PostalCodeDTO getPostalCode() {
		return this.postalCode;
	}

	public void setPostalCode(PostalCodeDTO postalCode) {
		this.postalCode = postalCode;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}