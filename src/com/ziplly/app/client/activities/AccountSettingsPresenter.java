package com.ziplly.app.client.activities;

import com.ziplly.app.model.AccountDTO;

public interface AccountSettingsPresenter<T extends AccountDTO> extends Presenter {
	void save(T account);
	void cancel();
}
