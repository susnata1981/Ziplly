package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Result;

public class GetLatLngResult implements Result {
	private double lat, lng;
	private String formattedAddress;

	public GetLatLngResult() {
	}
	
	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public String getFormattedAddress() {
		return formattedAddress;
	}

	public void setFormattedAddress(String formattedAddress) {
		this.formattedAddress = formattedAddress;
	}
}
