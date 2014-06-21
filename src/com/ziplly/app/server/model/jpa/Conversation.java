package com.ziplly.app.server.model.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.ConversationStatus;

@NamedQueries({
    @NamedQuery(
        name = "findConversationByAccountId",
        query = "from Conversation where receiver.accountId = :receiverAccountId or sender.accountId = :senderAccountId"),
    @NamedQuery(
        name = "findConversationCountByAccountIdAndStatus",
        query = "select count(*) from Conversation where receiver.accountId = :receiverAccountId and status = :status"),
    @NamedQuery(name = "findConversationById", query = "from Conversation where id = :id") })
@Entity
@Table(name = "conversation")
public class Conversation extends AbstractEntity {
	private static final long serialVersionUID = -2238086892572614374L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String subject;

	@OneToOne
	private Account sender;
	@OneToOne
	private Account receiver;

	@ElementCollection
	@CollectionTable(name = "conversation_messages", joinColumns = @JoinColumn(
	    name = "conversation_id"))
	private List<Message> messages = new ArrayList<Message>();

	private String status;

	public Conversation() {
	}

	public Conversation(ConversationDTO conversationDto) {
		this.id = conversationDto.getId();
		if (conversationDto.getSender() != null) {
			this.sender = new Account(conversationDto.getSender());
		}
		if (conversationDto.getReceiver() != null) {
			this.receiver = new Account(conversationDto.getReceiver());
		}
		this.subject = conversationDto.getSubject();
		this.setStatus(conversationDto.getStatus());
		this.setTimeCreated(conversationDto.getTimeCreated());
		this.setTimeUpdated(conversationDto.getTimeUpdated());
	}

	public void add(Message msg) {
		messages.add(msg);
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(ArrayList<Message> messages) {
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

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public ConversationStatus getStatus() {
		return ConversationStatus.valueOf(status);
	}

	public void setStatus(ConversationStatus status) {
		this.status = status.name();
	}
}