package com.ziplly.app.shared;

public enum EmailTemplate {
	WELCOME_REGISTRATION("welcome.ftl"),
	INVITE_PEOPLE("invite.ftl"),
	PASSWORD_RECOVERY("password_recovery.ftl");
	
	EmailTemplate(String filename) {
		this.setFilename(filename);
	}
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	private String filename;
}
