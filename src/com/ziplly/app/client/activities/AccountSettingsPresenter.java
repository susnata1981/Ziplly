package com.ziplly.app.client.activities;

import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.shared.UpdatePasswordAction;

public interface AccountSettingsPresenter<T extends AccountDTO> extends Presenter {
	void save(T account);
	void cancel();
	void updatePassword(UpdatePasswordAction action);
}
