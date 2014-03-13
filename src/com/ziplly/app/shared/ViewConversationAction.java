package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class ViewConversationAction implements Action<ViewConversationResult> {
	private Long conversationId;

	public ViewConversationAction() {
	}

	public ViewConversationAction(Long conversationId) {
		this.setConversationId(conversationId);
	}

	public Long getConversationId() {
		return conversationId;
	}

	public void setConversationId(Long conversationId) {
		this.conversationId = conversationId;
	}
}
