package com.ziplly.app.client.view.handler;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.ziplly.app.model.AccountDetails;

public class LoginHandler<T extends HandleLogin> implements AsyncCallback<AccountDetails> {
	private Logger logger = Logger.getLogger("LoginHandler");
	private T loginEventHandler;

	public LoginHandler(T object) {
		this.loginEventHandler = object;
	}

	@Override
	public void onFailure(Throwable caught) {
		logger.log(Level.SEVERE, "Failed to handle login:"+caught.getMessage());
	}

	@Override
	public void onSuccess(AccountDetails ad) {
		loginEventHandler.onLogin(ad);
	}
}