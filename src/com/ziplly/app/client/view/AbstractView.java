package com.ziplly.app.client.view;

import java.util.logging.Logger;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.Composite;
import com.ziplly.app.client.ZipllyService;
import com.ziplly.app.client.ZipllyServiceAsync;

public abstract class AbstractView extends Composite {
	protected Logger logger = Logger.getLogger("AbstractView");
	protected ZipllyServiceAsync service;
	protected SimpleEventBus eventBus;

	public AbstractView(SimpleEventBus eventBus) {
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
