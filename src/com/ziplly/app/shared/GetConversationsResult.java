package com.ziplly.app.shared;

import java.util.ArrayList;
import java.util.List;

import com.ziplly.app.model.ConversationDTO;

import net.customware.gwt.dispatch.shared.Result;

public class GetConversationsResult implements Result {
	private List<ConversationDTO> conversations = new ArrayList<ConversationDTO>();

	public GetConversationsResult() {
	}
	
	GetConversationsResult(List<ConversationDTO> conversations) {
		this.setConversations(conversations);
	}

	public List<ConversationDTO> getConversations() {
		return conversations;
	}

	public void setConversations(List<ConversationDTO> conversations) {
		this.conversations = conversations;
	}
}
