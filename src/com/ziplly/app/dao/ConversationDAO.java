package com.ziplly.app.dao;

import java.util.List;

import com.literati.app.shared.Account;

public interface ConversationDAO {
	List<ConversationDO> getConversationForAccount(int start, int length, Long accountId);
	boolean save(ConversationDO conversation);
	boolean save(Account sender, Account receiver, final MessageDO msg);
}
