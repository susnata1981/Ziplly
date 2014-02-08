package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

import com.ziplly.app.model.ConversationType;

public class GetConversationsAction implements Action<GetConversationsResult>{
	private ConversationType type = ConversationType.ALL;
	private Long conversationId;
	private int start;
	private int pageSize;
	private boolean getTotalConversation;
	
	public GetConversationsAction() {
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public boolean isGetTotalConversation() {
		return getTotalConversation;
	}

	public void setGetTotalConversation(boolean getTotalConversation) {
		this.getTotalConversation = getTotalConversation;
	}

	public ConversationType getType() {
		return type;
	}

	public void setType(ConversationType type) {
		this.type = type;
	}

	public Long getConversationId() {
		return conversationId;
	}

	public void setConversationId(Long conversationId) {
		this.conversationId = conversationId;
	}
}
