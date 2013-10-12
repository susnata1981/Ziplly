package com.ziplly.app.client.view;

import com.google.gwt.event.shared.SimpleEventBus;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.oauth.OAuthConfig;
import com.ziplly.app.client.oauth.OAuthFactory;
import com.ziplly.app.client.oauth.OAuthProvider;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.view.event.LogoutEvent;
import com.ziplly.app.client.view.handler.LoginEventHandler;
import com.ziplly.app.client.view.handler.LogoutEventHandler;
import com.ziplly.app.model.AccountDTO;

public abstract class AbstractAccountView extends AbstractView {
	protected OAuthConfig authConfig = OAuthFactory.getAuthConfig(OAuthProvider.FACEBOOK.name());
	protected AccountDTO account;
	
	public AbstractAccountView(CachingDispatcherAsync dispatcher, SimpleEventBus eventBus) {
		super(dispatcher, eventBus);
	}

	@Override
	protected void setupCommonHandlers() {
		eventBus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {
			@Override
			public void onEvent(LoginEvent event) {
				onUserLogin(event.getAccount());
			}
		});
		
		eventBus.addHandler(LogoutEvent.TYPE, new LogoutEventHandler() {
			@Override
			public void onEvent(LogoutEvent event) {
				AbstractAccountView.this.account = null;
			}
		});
	}
	
	final protected void onUserLogin(AccountDTO accountDTO) {
		this.setAccount(accountDTO);
		internalOnUserLogin();
	}

	@Override
	protected void setupHandlers() {
	}
	
	protected abstract void internalOnUserLogin();

	public AccountDTO getAccount() {
		return account;
	}

	public void setAccount(AccountDTO account) {
		this.account = account;
	}
	
	public boolean isUserLoggedIn() {
		return account != null;
	}
}
