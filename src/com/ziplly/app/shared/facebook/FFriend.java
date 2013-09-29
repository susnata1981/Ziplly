package com.ziplly.app.shared.facebook;

import java.io.Serializable;

import com.restfb.Facebook;

public class FFriend implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Facebook
	public String uid;
	@Facebook("first_name")
	public String firstName;
	@Facebook("current_location")
	public FCurrentLocation currentLocation;
}
