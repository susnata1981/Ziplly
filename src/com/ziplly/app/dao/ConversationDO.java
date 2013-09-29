package com.ziplly.app.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.literati.app.shared.Account;

@Entity
public class ConversationDO implements Serializable {
	private static final long serialVersionUID = -2238086892572614374L;
	@Id
	private Long id;
	@Index
	@Load
	private Ref<Account> sender;
	@Index
	@Load
	private Ref<Account> receiver;
	@Load
	private List<Ref<MessageDO>> messages = new ArrayList<Ref<MessageDO>>();
	
	public void add(MessageDO msg) {
		messages.add(Ref.create(Key.create(MessageDO.class, msg.getId())));
	}

	public List<MessageDO> getMessages() {
		List<MessageDO> response = Lists.newArrayList();
		for(Ref<MessageDO> msg : messages) {
			response.add(msg.get());
		}
		return response;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Account getSender() {
		return sender.get();
	}
	public void setSender(Account sender) {
		this.sender = Ref.create(Key.create(Account.class,sender.getId()));
	}
	public void setSender(Ref<Account> sender) {
		this.sender = sender;
	}
	public void setSender(Long accountId) {
		this.sender = Ref.create(Key.create(Account.class,accountId));
	}
	public Account getReceiver() {
		return receiver.get();
	}
	public void setReceiver(Account receiver) {
		this.receiver = Ref.create(Key.create(Account.class,receiver.getId()));
	}
	public void setReceiver(Ref<Account> receiver) {
		this.receiver = receiver;
	}
	public void setReceiver(Long receiverId) {
		this.receiver = Ref.create(Key.create(Account.class,receiverId));
	}
	public Ref<Account> getSenderKey() {
		return sender;
	}
	public Ref<Account> getReceiverKey() {
		return receiver;
	}
}
