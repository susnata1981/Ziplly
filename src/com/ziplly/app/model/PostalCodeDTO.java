package com.ziplly.app.model;

import java.io.Serializable;

public class PostalCodeDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long postalCodeId;
	private String postalCode;
	private String state;
	private String city;
	private String fullState;
	private String latitude;
	private String longitude;

	public PostalCodeDTO() {
	}

	public Long getPostalCodeId() {
		return postalCodeId;
	}

	@Override
	public String toString() {
		return "(" + this.postalCode + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof PostalCodeDTO)) {
			return false;
		}

		PostalCodeDTO p = (PostalCodeDTO) o;
		return p.getPostalCode() == this.getPostalCode();
	}

	public void setPostalCodeId(Long postalCodeId) {
		this.postalCodeId = postalCodeId;
	}

	// to be overridden
	public String getDisplayName() {
		return city + "," + state;
	}

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

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
}
