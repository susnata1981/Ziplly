package com.ziplly.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Conversation implements Serializable {
	private static final long serialVersionUID = -2238086892572614374L;
	private Long id;
	private List<Message> messages = new ArrayList<Message>();
	private Account sender;
	private Account receiver;
	
	public void add(Message msg) {
		messages.add(msg);
	}
	public List<Message> getMessages() {
		return messages;
	}
	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
	public void setSender(Account sender) {
		this.sender = sender;
	}
	public Account getSender() {
		return sender;
	}
	public void setReceiver(Account receiver) {
		this.receiver = receiver;
	}
	public Account getReceiver() {
		return receiver;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
}
