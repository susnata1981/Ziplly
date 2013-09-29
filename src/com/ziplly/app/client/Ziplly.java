package com.ziplly.app.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Ziplly implements EntryPoint {
	private final SimpleEventBus eventBus = new SimpleEventBus();
	private MainController controller;
	private Logger logger = Logger.getLogger("ziplly");
	
	
	@Override
	public void onModuleLoad() {
		logger.log(Level.SEVERE, "Ziplly onModuleLoad initiated");
		
		controller = new MainController(
				RootPanel.get("main"),
				eventBus);
		controller.go();
	}
}
