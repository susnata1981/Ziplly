package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.model.Conversation;
import com.ziplly.app.model.ConversationDTO;

public interface ConversationDAO {
	void save(Conversation conversation);
	List<ConversationDTO> getConversationForAccount(Long accountId);
}
