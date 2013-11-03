package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

import com.ziplly.app.model.ConversationDTO;

public class SendMessageAction implements Action<SendMessageResult> {
	private ConversationDTO conversation;

	public SendMessageAction() {
	}
	
	public SendMessageAction(ConversationDTO conversation) {
		this.setConversation(conversation);
	}

	public ConversationDTO getConversation() {
		return conversation;
	}

	public void setConversation(ConversationDTO conversation) {
		this.conversation = conversation;
	}
}
