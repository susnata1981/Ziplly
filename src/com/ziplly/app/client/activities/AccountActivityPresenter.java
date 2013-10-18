package com.ziplly.app.client.activities;

import com.ziplly.app.model.AccountDTO;

public interface AccountActivityPresenter extends Presenter, LoginWidgetPresenter {
	void logout();
	void save(AccountDTO account);
	void displayPublicProfile(Long accountId);
	void tweet(String content);
}
