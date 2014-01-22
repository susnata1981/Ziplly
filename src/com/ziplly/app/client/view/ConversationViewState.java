package com.ziplly.app.client.view;

import com.google.gwt.view.client.Range;
import com.ziplly.app.model.ConversationType;
import com.ziplly.app.shared.GetConversationsAction;

public class ConversationViewState {
	private int start;
	private boolean totalMessageCountRequired;
	private ConversationType conversationType = ConversationType.RECEIVED;
	private int pageSize;
	private GetConversationsAction action;
	
	public ConversationViewState(int pageSize) {
		action = new GetConversationsAction();
		totalMessageCountRequired = true;
		this.pageSize = pageSize;
	}
	
	public void reset() {
		start = 0;
		conversationType = ConversationType.RECEIVED;
		totalMessageCountRequired = true;
	}
	
	public void onRangeChange(Range range) {
		start = range.getStart();
		totalMessageCountRequired = false;
	}
	
	public void getReceivedMessages() {
		reset();
	}
	
	public void getSentMessages() {
		start = 0;
		conversationType = ConversationType.SENT;
		totalMessageCountRequired = true;
	}
	
	public GetConversationsAction getSearchCriteria() {
		action.setStart(start);
		action.setType(conversationType);
		action.setGetTotalConversation(totalMessageCountRequired);
		action.setPageSize(pageSize);
		return action;
	}
	
	public int getStart() {
		return start;
	}
	
	public void setStart(int start) {
		this.start = start;
	}
	
	public ConversationType getConversationType() {
		return conversationType;
	}
	
	public void setConversationType(ConversationType conversationType) {
		this.conversationType = conversationType;
	}
}
