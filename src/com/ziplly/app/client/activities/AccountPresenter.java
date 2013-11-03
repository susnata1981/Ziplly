package com.ziplly.app.client.activities;

import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.TweetDTO;

public interface AccountPresenter<T extends AccountDTO> extends Presenter {
	void save(T account);
	void displayPublicProfile();
	void displayProfile();
	void tweet(TweetDTO tweet);
	void logout();
	void displayPublicProfile(T account);
	void sendMessage(ConversationDTO conversation);
}
