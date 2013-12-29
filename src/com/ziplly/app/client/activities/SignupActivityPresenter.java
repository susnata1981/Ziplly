package com.ziplly.app.client.activities;

import com.ziplly.app.model.AccountDTO;

public interface SignupActivityPresenter extends LoginPresenter {
	void onFacebookLogin();
	void register(AccountDTO account);
	void setImageUploadUrl();
	void setUploadImageHandler();
	void register(AccountDTO account, String code);
	void getNeighborhoodData(String postalCode);
	void addToInviteList(String email, String postalCode);
}
