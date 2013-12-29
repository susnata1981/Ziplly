package com.ziplly.app.model;

import java.io.Serializable;
import java.util.Date;

public class AccountNotificationDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long notificationId;
	private AccountDTO sender;
	private AccountDTO recipient;
	private ReadStatus readStatus;
	private RecordStatus status;
	private NotificationType type;
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

	public AccountDTO getRecipient() {
		return recipient;
	}

	public void setRecipient(AccountDTO receiver) {
		this.recipient = receiver;
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

	public Date getTimeUpdated() {
		return timeUpdated;
	}

	public void setTimeUpdated(Date timeUpdated) {
		this.timeUpdated = timeUpdated;
	}

	public RecordStatus getStatus() {
		return status;
	}

	public void setStatus(RecordStatus status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((notificationId == null) ? 0 : notificationId.hashCode());
		result = prime * result + ((readStatus == null) ? 0 : readStatus.hashCode());
		result = prime * result + ((recipient == null) ? 0 : recipient.hashCode());
		result = prime * result + ((sender == null) ? 0 : sender.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((timeCreated == null) ? 0 : timeCreated.hashCode());
		result = prime * result + ((timeUpdated == null) ? 0 : timeUpdated.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AccountNotificationDTO other = (AccountNotificationDTO) obj;
		if (notificationId == null) {
			if (other.notificationId != null)
				return false;
		} 
		else if (notificationId.equals(other.notificationId)) {
			return true;
		}
		
		return false;
	}
	
	
}
