package com.ziplly.app.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="notification_settings")
public class AccountNotificationSettings {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long notificationId;
	
	@ManyToOne
	@JoinColumn(name="account_id")
	private Account account;
	
	private String type;
	
	private String action;

	@Column(name="time_created")
	private Date timeCreated;
	
	public AccountNotificationSettings() {
	}
	
	public AccountNotificationSettings(AccountNotificationSettingsDTO an) {
		this.notificationId = an.getNotificationId();
		// hack to get around infinite call
		Account acct = new Account();
		acct.setAccountId(an.getAccount().getAccountId());
		this.account = acct;
		this.setType(an.getType());
		this.action = an.getAction().name();
		this.timeCreated = an.getTimeCreated();
	}

	public Long getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(Long notificationId) {
		this.notificationId = notificationId;
	}

	public NotificationType getType() {
		return NotificationType.valueOf(type);
	}

	public void setType(NotificationType type) {
		this.type = type.name();
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}

	public NotificationAction getAction() {
		return NotificationAction.valueOf(action);
	}

	public void setAction(NotificationAction action) {
		this.action = action.name();
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
}