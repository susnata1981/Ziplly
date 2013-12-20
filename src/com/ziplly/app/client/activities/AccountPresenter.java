package com.ziplly.app.client.activities;

import com.ziplly.app.model.AccountDTO;

public interface AccountPresenter<T extends AccountDTO> extends TweetPresenter, EmailPresenter {
	void save(T account);
	void displayProfile();
	void logout();
//	void sendMessage(ConversationDTO conversation);
	void settingsLinkClicked();
	void messagesLinkClicked();
}
