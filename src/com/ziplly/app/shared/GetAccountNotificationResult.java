package com.ziplly.app.shared;

import java.util.List;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.AccountNotificationDTO;

public class GetAccountNotificationResult implements Result {
	private List<AccountNotificationDTO> accountNotifications;

	public GetAccountNotificationResult() {
	}
	
	public GetAccountNotificationResult(List<AccountNotificationDTO> notifications) {
		this.setAccountNotifications(notifications);
	}

	public List<AccountNotificationDTO> getAccountNotifications() {
		return accountNotifications;
	}

	public void setAccountNotifications(List<AccountNotificationDTO> accountNotifications) {
		this.accountNotifications = accountNotifications;
	}
}
