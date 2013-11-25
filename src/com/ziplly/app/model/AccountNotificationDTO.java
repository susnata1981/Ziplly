package com.ziplly.app.model;

import java.io.Serializable;
import java.util.Date;

public class AccountNotificationDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long notificationId;
	private AccountDTO sender;
	private AccountDTO receiver;
	private ReadStatus readStatus;
	private NotificationType type;
	private NotificationAction action;
	private Date timeUpdated;
	private Date timeCreated;
	public Long getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(Long notificationId) {
		this.notificationId = notificationId;
	}

	public AccountDTO getSender() {
		return sender;
	}

	public void setSender(AccountDTO sender) {
		this.sender = sender;
	}

	public AccountDTO getReceiver() {
		return receiver;
	}

	public void setReceiver(AccountDTO receiver) {
		this.receiver = receiver;
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

	public ReadStatus getReadStatus() {
		return readStatus;
	}

	public void setReadStatus(ReadStatus readStatus) {
		this.readStatus = readStatus;
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
}
