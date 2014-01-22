package com.ziplly.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

public class ConversationDTO implements Serializable {
	private static final long serialVersionUID = -2238086892572614374L;
	private Long id;
	private List<MessageDTO> messages = new ArrayList<MessageDTO>();
	private AccountDTO sender;
	private AccountDTO receiver;
	private String subject;
	private String status;
	private Date timeUpdated;
	private Date timeCreated;
	@Transient
	private boolean isSender;
	
	public void add(MessageDTO msg) {
		messages.add(msg);
	}
	public List<MessageDTO> getMessages() {
		return messages;
	}
	public void setMessages(ArrayList<MessageDTO> messages) {
		this.messages = messages;
	}
	public void setSender(AccountDTO sender) {
		this.sender = sender;
	}
	public AccountDTO getSender() {
		return sender;
	}
	public void setReceiver(AccountDTO receiver) {
		this.receiver = receiver;
	}
	public AccountDTO getReceiver() {
		return receiver;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getTimeUpdated() {
		return timeUpdated;
	}
	public void setTimeUpdated(Date timeUpdated) {
		this.timeUpdated = timeUpdated;
	}
	public Date getTimeCreated() {
		return timeCreated;
	}
	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public boolean isSender() {
		return isSender;
	}
	public void setIsSender(boolean isSender) {
		this.isSender = isSender;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof ConversationDTO)){
			return false;
		}
		
		return ((ConversationDTO)o).getId() == this.id;
	}
	
	@Override
	public int hashCode() {
		return (int) (Math.pow(id, 3)*123+124);
	}
	
	public ConversationStatus getStatus() {
		return ConversationStatus.valueOf(status);
	}
	public void setStatus(ConversationStatus status) {
		this.status = status.name();
	}
}
