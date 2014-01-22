package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class AddInvitationAction implements Action<AddInvitationResult>{
	private int zip;
	private String email;

	public AddInvitationAction() {
	}
	
	public AddInvitationAction(String email, int zip) {
		this.setEmail(email);
		this.setZip(zip);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getZip() {
		return zip;
	}

	public void setZip(int zip) {
		this.zip = zip;
	}
}
