package com.ziplly.app.client.activities;

import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.ConversationDTO;

public interface AccountPresenter<T extends AccountDTO> extends TweetPresenter, EmailPresenter {
	void save(T account);
	void displayProfile();
//	public void displayPublicProfile(final Long accountId);
	void logout();
	void sendMessage(ConversationDTO conversation);
	void settingsLinkClicked();
	void messagesLinkClicked();
}
