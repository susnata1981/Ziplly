package com.ziplly.app.client.activities;

import com.ziplly.app.model.AccountDTO;

public interface SignupActivityPresenter extends Presenter, LoginWidgetPresenter {
	void onFacebookLogin();
	void register(AccountDTO account);
	void setImageUploadUrl();
	void setUploadImageHandler();
}
