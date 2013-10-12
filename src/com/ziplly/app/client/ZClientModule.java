package com.ziplly.app.client;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.view.AccountView;
import com.ziplly.app.client.view.HomeView;
import com.ziplly.app.client.view.MainView;
import com.ziplly.app.client.view.SignupView;
import com.ziplly.app.client.widget.LoginWidget;
import com.ziplly.app.client.widget.LogoutWidget;

public class ZClientModule extends AbstractGinModule {

	@Override
	protected void configure() {
		bind(CachingDispatcherAsync.class).in(Singleton.class);
		bind(SimpleEventBus.class).in(Singleton.class);
		
		// main presenter
		bind(MainController.class).in(Singleton.class);
		
		// views
		bind(AccountView.class).in(Singleton.class);
		bind(HomeView.class).in(Singleton.class);
		bind(SignupView.class).in(Singleton.class);
		bind(MainView.class).in(Singleton.class);
		
		// widgets
		bind(LoginWidget.class).in(Singleton.class);
		bind(LogoutWidget.class).in(Singleton.class);
	}
}
