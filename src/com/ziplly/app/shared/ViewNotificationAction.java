package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

import com.ziplly.app.model.AccountNotificationDTO;

public class ViewNotificationAction implements Action<ViewNotificationResult>{
	private AccountNotificationDTO accountNotification;

	public ViewNotificationAction() {
	}
	
	public ViewNotificationAction(AccountNotificationDTO an) {
		this.setAccountNotification(an);
	}

	public AccountNotificationDTO getAccountNotification() {
		return accountNotification;
	}

	public void setAccountNotification(AccountNotificationDTO accountNotification) {
		this.accountNotification = accountNotification;
	}

}
