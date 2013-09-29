package com.ziplly.app.client.view;

import com.google.gwt.event.shared.SimpleEventBus;
import com.ziplly.app.client.oauth.OAuthConfig;
import com.ziplly.app.client.oauth.OAuthFactory;
import com.ziplly.app.client.oauth.OAuthProvider;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.view.handler.LoginEventHandler;
import com.ziplly.app.model.AccountDetails;

public abstract class AbstractAccountView extends AbstractView {
	private AccountDetails ad;
	protected OAuthConfig authConfig = OAuthFactory.getAuthConfig(OAuthProvider.FACEBOOK.name());
	
	
	public AbstractAccountView(SimpleEventBus eventBus) {
		super(eventBus);
	}

	@Override
	protected void setupCommonHandlers() {
		eventBus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {
			@Override
			public void onEvent(LoginEvent event) {
				onUserLogin(event.getAccountDetails());
			}
		});
	}
	
	final protected void onUserLogin(AccountDetails ad) {
		this.setAd(ad);
		internalOnUserLogin();
	}

	@Override
	protected void setupHandlers() {
	}
	
	protected abstract void internalOnUserLogin();

	public AccountDetails getAd() {
		return ad;
	}

	public void setAd(AccountDetails ad) {
		this.ad = ad;
	}
}
