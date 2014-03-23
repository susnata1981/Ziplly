package com.ziplly.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "location")
public class Location extends AbstractTimestampAwareEntity {
	private static final long serialVersionUID = 1L;
	@Column(name = "location_id")
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long locationId;

	@Column(name = "type")
	private String type;

	@Column(nullable = true)
	private String address;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "neighborhood_id", nullable = false)
	private Neighborhood neighborhood;

	public Location() {
	}

	public Location(LocationDTO location) {
		this.setLocationId(location.getLocationId());
		this.address = location.getAddress();
		this.setType(location.getType());
		this.neighborhood = new Neighborhood(location.getNeighborhood());
		setTimeCreated(location.getTimeCreated());
		setTimeUpdated(location.getTimeUpdated());
	}

	public Neighborhood getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(Neighborhood neighborhood) {
		this.neighborhood = neighborhood;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

		if (!(o instanceof Location)) {
			return false;
		}

		Location l = (Location) o;
		return l.getLocationId() == locationId;
	}

	@Override
	public int hashCode() {
		return neighborhood.hashCode();
	}

	public LocationType getType() {
		return LocationType.valueOf(type);
	}

	public void setType(LocationType type) {
		this.type = type.name();
	}
}
