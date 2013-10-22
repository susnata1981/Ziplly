package com.ziplly.app.client.view;

import com.ziplly.app.client.activities.AccountActivityPresenter;
import com.ziplly.app.model.AccountDTO;

public interface IAccountView<T extends AccountDTO> extends View<AccountActivityPresenter> {
	void displayLoginWidget();
	void displayLogoutWidget();
	void displayProfile(T account);
	void displayPublicProfile(T account);
	void onSave();
}
