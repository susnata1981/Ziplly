package com.ziplly.app.client.view;

import java.util.List;

import com.ziplly.app.client.activities.AccountPresenter;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.ConversationDTO;

public interface IAccountView<T extends AccountDTO> extends View<AccountPresenter<T>> {

	void displayProfile(T account);

	void displayPublicProfile(T account);
	
	void displayAccountUpdateSuccessfullMessage();
	
	void displayAccountUpdateFailedMessage();
	
	void clearTweet();
	
	void displayLogoutWidget();

	void closeSendMessageWidget();
	
	void displayConversations(List<ConversationDTO> conversations);
}
