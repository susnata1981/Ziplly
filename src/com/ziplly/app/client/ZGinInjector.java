package com.ziplly.app.client;

import net.customware.gwt.dispatch.client.gin.StandardDispatchModule;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.place.shared.PlaceController;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.view.AccountView;
import com.ziplly.app.client.view.HomeView;
import com.ziplly.app.client.view.SignupView;
import com.ziplly.app.client.widget.LoginWidget;

@GinModules({StandardDispatchModule.class, ZClientModule.class})
public interface ZGinInjector extends Ginjector{
	CachingDispatcherAsync getCachingDispatcher();
	ZipllyController getZipllyController();
	SimpleEventBus getEventBus();
	
	// views
	SignupView getSignupView();
	HomeView getHomeView();
	AccountView getAccountView();
	
	// widgets
	LoginWidget getLoginWidget();
	
	// Place controller
	PlaceController getPlaceController();
}
