package com.ziplly.app.model;

import javax.persistence.Column;
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
public class AccountNotification extends AbstractTimestampAwareEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="notification_id")
	private Long notificationId;
	
	@ManyToOne
	@JoinColumn(name="recipient_id")
	private Account recipient;
	
	@OneToOne
	private Account sender;

	@OneToOne
	@JoinColumn(name="tweet_id")
	private Tweet tweet;
	
	@Column(name="read_status")
	private ReadStatus readStatus;
	
	@Column(name="record_status")
	private RecordStatus status;
	
	@Column(name="notification_type")
	private NotificationType type;
	
	public AccountNotification() {
	}
	
	public AccountNotification(AccountNotificationDTO an) {
		this.notificationId = an.getNotificationId();
		
		this.recipient = new Account();
		this.recipient.setAccountId(an.getRecipient().getAccountId());
		
		this.sender = new Account();
		this.sender.setAccountId(an.getSender().getAccountId());
		
		this.tweet = new Tweet(an.getTweet());
		this.readStatus = an.getReadStatus();
		this.type = an.getType();
		this.setTimeUpdated(an.getTimeUpdated());
		this.setTimeCreated(an.getTimeCreated());
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

	public Account getRecipient() {
		return recipient;
	}

	public void setRecipient(Account receiver) {
		this.recipient = receiver;
	}

	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}

	public ReadStatus getReadStatus() {
		return readStatus;
	}

	public void setReadStatus(ReadStatus readStatus) {
		this.readStatus = readStatus;
	}

	public RecordStatus getStatus() {
		return status;
	}

	public void setStatus(RecordStatus status) {
		this.status = status;
	}

	public Tweet getTweet() {
		return tweet;
	}

	public void setTweet(Tweet tweet) {
		this.tweet = tweet;
	}
}
