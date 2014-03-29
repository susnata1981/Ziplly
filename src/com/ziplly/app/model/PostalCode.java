package com.ziplly.app.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@NamedQueries({
    @NamedQuery(name = "findPostalCodeById",
        query = "from PostalCode p where p.postalCode = :postalCodeId"),
    @NamedQuery(name = "findAllPostalCodes", query = "from PostalCode") })
@Entity
@Table(name = "postalcode")
public class PostalCode implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "zip", length=10)
	private String postalCode;

	@Column(name = "state", updatable = false)
	private String state;

	@Column(name = "city", updatable = false)
	private String city;

	@Column(name = "full_state", updatable = false)
	private String fullState;

	@Column(name = "latitude", updatable = false)
	private String latitude;

	@Column(name = "longitude", updatable = false)
	private String longitude;

	public PostalCode() {
	}

	public PostalCode(PostalCodeDTO postalCode) {
		this.setPostalCode(postalCode.getPostalCode());
		this.setLatitude(postalCode.getLatitude());
		this.setLongitude(postalCode.getLongitude());
		this.setCity(postalCode.getCity());
		this.setState(postalCode.getState());
		this.setFullState(postalCode.getFullState());
	}

	// public Long getPostalCodeId() {
	// return this.postalCodeId;
	// }
	//
	// public void setPostalCodeId(Long postalCodeId) {
	// this.postalCodeId = postalCodeId;
	// }

	public String getPostalCode() {
		return this.postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getFullState() {
		return fullState;
	}

	public void setFullState(String fullState) {
		this.fullState = fullState;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
}