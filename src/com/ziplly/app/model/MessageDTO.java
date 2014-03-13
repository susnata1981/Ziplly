package com.ziplly.app.model;

import java.io.Serializable;
import java.util.Date;

public class MessageDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private ConversationDTO conversation;
	private String message;
	private AccountDTO sender;
	private AccountDTO receiver;
	private Date timeCreated;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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

	public ConversationDTO getConversation() {
		return conversation;
	}

	public void setConversation(ConversationDTO conversation) {
		this.conversation = conversation;
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}
}
