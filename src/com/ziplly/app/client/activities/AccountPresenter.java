package com.ziplly.app.client.activities;

import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.shared.TweetResult;

public interface AccountPresenter<T extends AccountDTO> extends TweetPresenter {
	void save(T account);
	void displayProfile();
//	public void displayPublicProfile(final Long accountId);
	void logout();
	void sendMessage(ConversationDTO conversation);
	void settingsLinkClicked();
	void messagesLinkClicked();
}
