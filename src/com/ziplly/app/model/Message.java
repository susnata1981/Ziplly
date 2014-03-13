package com.ziplly.app.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Embeddable
public class Message implements Serializable {
	private static final long serialVersionUID = 1L;

	public Message() {
	}

	@Column(length = 1024)
	private String message;

	@OneToOne
	@JoinColumn(name = "sender_id")
	private Account sender;

	@OneToOne
	@JoinColumn(name = "receiver_id")
	private Account receiver;

	private Date timeCreated;

	public Message(MessageDTO m) {
		this.message = m.getMessage();
		this.sender = new Account(m.getSender());
		this.receiver = new Account(m.getReceiver());
		this.timeCreated = m.getTimeCreated();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}
}
