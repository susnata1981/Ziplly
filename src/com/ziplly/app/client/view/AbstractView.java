package com.ziplly.app.client.view;

import java.util.logging.Logger;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.Composite;
import com.ziplly.app.client.ZipllyService;
import com.ziplly.app.client.ZipllyServiceAsync;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;

public abstract class AbstractView extends Composite {
	protected Logger logger = Logger.getLogger("AbstractView");
	protected ZipllyServiceAsync service;
	protected CachingDispatcherAsync dispatcher;
	protected SimpleEventBus eventBus;

	public AbstractView(CachingDispatcherAsync dispatcher, SimpleEventBus eventBus) {
		this.dispatcher = dispatcher;
		this.service = GWT.create(ZipllyService.class);
		this.eventBus = eventBus;
		setupUiElements();
		initWidget();
		setupCommonHandlers();
		setupHandlers();
	}
	
	protected void setupCommonHandlers() {
	}

	@Override
	public void onLoad() {
		postInitWidget();
	}
	
	protected abstract void initWidget();
	protected abstract void postInitWidget();
	
	protected abstract void setupUiElements();
	
	protected void setupHandlers() {
	}

	public ZipllyServiceAsync getService() {
		return service;
	}

	public void setService(ZipllyServiceAsync service) {
		this.service = service;
	}
}
