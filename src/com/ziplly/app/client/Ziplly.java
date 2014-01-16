package com.ziplly.app.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Ziplly implements EntryPoint {
	private Logger logger = Logger.getLogger(Ziplly.class.getName());
	ZGinInjector injector = GWT.create(ZGinInjector.class);
	CachingDispatcherAsync dispatcher = injector.getCachingDispatcher();
	ZipllyController zcontroller = injector.getZipllyController();
	
	@Override
	public void onModuleLoad() {
		logger.log(Level.INFO, "Ziplly onModuleLoad initiated");
		zcontroller.go();
	}
}
