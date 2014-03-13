package com.ziplly.app.model;

import java.io.Serializable;
import java.util.Date;

public class AccountNotificationSettingsDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long notificationId;
	private AccountDTO account;
	private NotificationType type;
	private NotificationAction action;
	private Date timeUpdated;
	private Date timeCreated;

	public AccountNotificationSettingsDTO() {
	}

	public Long getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(Long notificationId) {
		this.notificationId = notificationId;
	}

	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}

	public NotificationAction getAction() {
		return action;
	}

	public void setAction(NotificationAction action) {
		this.action = action;
	}

	public Date getTimeUpdated() {
		return timeUpdated;
	}

	public void setTimeUpdated(Date timeUpdated) {
		this.timeUpdated = timeUpdated;
	}

	public AccountDTO getAccount() {
		return account;
	}

	public void setAccount(AccountDTO account) {
		this.account = account;
	}
}
