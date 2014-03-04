package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class VerifyEmailAction implements Action<VerifyEmailResult> {
	private String code;
	private String email;
	private Long id;
	public VerifyEmailAction() {
	}
	
	public VerifyEmailAction(Long id, String code) {
		this.setId(id);
		this.code = code;
	}

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
}
