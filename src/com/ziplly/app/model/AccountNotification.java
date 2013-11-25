package com.ziplly.app.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="account_notification")
public class AccountNotification {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long notificationId;
	
	@OneToOne
	private Account sender;
	
	@ManyToOne
	@JoinColumn(name="receiver_id")
	private Account receiver;
	
	private ReadStatus readStatus;
	
	private NotificationType type;
	
	private NotificationAction action;
	
	private Date timeUpdated;
	private Date timeCreated;
	
	public AccountNotification() {
	}
	
	public AccountNotification(AccountNotificationDTO an) {
		this.notificationId = an.getNotificationId();
		this.receiver = new Account(an.getReceiver());
		this.sender = new Account(an.getSender());
		this.readStatus = an.getReadStatus();
		this.type = an.getType();
		this.action = an.getAction();
		this.timeUpdated = an.getTimeUpdated();
		this.timeCreated = an.getTimeCreated();
	}

	public Long getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(Long notificationId) {
		this.notificationId = notificationId;
	}

	public Account getSender() {
		return sender;
	}

	public void setSender(Account sender) {
		this.sender = sender;
	}

	public Account getReceiver() {
		return receiver;
	}

	public void setReceiver(Account receiver) {
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
