package com.ziplly.app.client.activities;

import com.ziplly.app.client.view.CouponTransactionView.CouponTransactionPresenter;
import com.ziplly.app.model.AccountDTO;

public interface AccountPresenter<T extends AccountDTO> extends CouponTransactionPresenter {
	void save(T account);

	void displayProfile();

	void logout();

	// void sendMessage(ConversationDTO conversation);
	void settingsLinkClicked();

	void messagesLinkClicked();

	SendMessagePresenter getSendMessagePresenter();
}
