package com.ziplly.app.client.activities;

import com.ziplly.app.client.view.account.CouponTransactionViewPresenter;
import com.ziplly.app.model.AccountDTO;

public interface AccountPresenter<T extends AccountDTO> extends CouponTransactionViewPresenter {
	void save(T account);

	void displayProfile();

	void logout();

	void settingsLinkClicked();

	void messagesLinkClicked();

	SendMessagePresenter getSendMessagePresenter();
}
