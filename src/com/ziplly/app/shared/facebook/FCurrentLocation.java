package com.ziplly.app.shared.facebook;

import java.io.Serializable;

import com.restfb.Facebook;

public class FCurrentLocation implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Facebook
	public String city;
	@Facebook
	public String state;
	@Facebook
	public String zip;
	@Facebook
	public String country;
	@Facebook
	public String longitude;
	@Facebook
	public String latitude;
}
