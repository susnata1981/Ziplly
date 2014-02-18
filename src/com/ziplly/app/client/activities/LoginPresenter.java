package com.ziplly.app.client.activities;

import com.ziplly.app.client.ApplicationContext.Environment;

public interface LoginPresenter extends Presenter {
	void onLogin(String username, String password);

	Environment getEnvironment();
}
