package com.ziplly.app.shared;

import java.util.List;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.AccountNotificationDTO;

public class ViewNotificationResult implements Result {
	private List<AccountNotificationDTO> accountNotifications;

	public List<AccountNotificationDTO> getAccountNotifications() {
		return accountNotifications;
	}

	public void setAccountNotifications(List<AccountNotificationDTO> accountNotifications) {
		this.accountNotifications = accountNotifications;
	}
}
