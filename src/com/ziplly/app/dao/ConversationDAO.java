package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.model.Conversation;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.ConversationType;

public interface ConversationDAO {
	Conversation save(Conversation conversation);

	List<ConversationDTO> getConversationForAccount(Long accountId,
	    ConversationType conversationType,
	    int start,
	    int pageSize);

	void markConversationAsRead(Long conversationId);

	Long getUnreadConversationCountForAccount(Long accountId);

	Long getTotalConversationCount(Long accountId);

	ConversationDTO findConversationById(Long conversationId);

	Long getTotalConversationCountOfType(ConversationType type, Long accountId);
}
