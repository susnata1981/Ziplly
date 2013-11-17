package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class ResetPasswordAction implements Action<ResetPasswordResult>{
	private Long accountId;
	private String password;

	public ResetPasswordAction() {
	}
	
	public ResetPasswordAction(String password) {
		this.setPassword(password);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
}
