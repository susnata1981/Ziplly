package com.ziplly.app.client;

import net.customware.gwt.dispatch.client.gin.StandardDispatchModule;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.place.shared.PlaceController;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;

@GinModules({ StandardDispatchModule.class, ZClientModule.class })
public interface ZGinInjector extends Ginjector {
	
	CachingDispatcherAsync getCachingDispatcher();

	ZipllyController getZipllyController();

	PlaceController getPlaceController();
}
