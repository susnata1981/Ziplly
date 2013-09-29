package com.ziplly.app.dao;

import java.io.Serializable;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class MessageDO implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private Long id;
	@Index
	@Parent
	@Load
	private Ref<ConversationDO> conversation;
	private String message;
	private String subject;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Ref<ConversationDO> getConversation() {
		return conversation;
	}
	public void setConversation(Ref<ConversationDO> conversation) {
		this.conversation = conversation;
	}
	public void setConversation(ConversationDO conversation) {
		this.conversation = Ref.create(Key.create(ConversationDO.class, conversation.getId()));
	}
}
