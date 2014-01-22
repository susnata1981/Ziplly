package com.ziplly.app.model;

public class PendingInvitationsDTO {
	private Long id;
	private String email;
	private int zip;

	public PendingInvitationsDTO() {
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
