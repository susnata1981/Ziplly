package com.ziplly.app.client.view;

import com.ziplly.app.client.activities.AccountActivityPresenter;
import com.ziplly.app.model.AccountDTO;

public interface IAccountView extends View<AccountActivityPresenter> {
	void display(AccountDTO account);
	void onSave();
}
