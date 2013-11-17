package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class UpdatePasswordAction implements Action<UpdatePasswordResult>{
	private String newPassword;
	private String oldPassword;

	public UpdatePasswordAction() {
	}
	
	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

}
