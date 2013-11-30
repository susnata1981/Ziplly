package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Result;

public class CreateRegistrationResult implements Result {
	private String registrationLink;

	public CreateRegistrationResult() {
	}
	
	public CreateRegistrationResult(String link) {
		this.registrationLink = link;
	}
	
	public String getRegistrationLink() {
		return registrationLink;
	}

	public void setRegistrationLink(String registrationLink) {
		this.registrationLink = registrationLink;
	}
}
