package com.ziplly.app.model;

import java.io.Serializable;

public class PostalCodeDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long postalCodeId;
	private String postalCode;
	private String city;
	private String state;
	
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
		
		if (!(o instanceof PostalCode)) {
			return false;
		}
		
		PostalCode p = (PostalCode)o;
		return p.getPostalCodeId() == this.getPostalCodeId();
	}

	public void setPostalCodeId(Long postalCodeId) {
		this.postalCodeId = postalCodeId;
	}

	// to be overridden
	public String getDisplayName() {
		return "<NAME>";
	}

	public String getPostalCode() {
		return this.postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
}
