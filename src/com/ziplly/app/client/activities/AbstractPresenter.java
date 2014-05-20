package com.ziplly.app.client.activities;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;

public abstract class AbstractPresenter implements Presenter {
	protected CachingDispatcherAsync dispatcher;
	protected EventBus eventBus;
	protected PlaceController placeController;
	protected ApplicationContext ctx;

	public AbstractPresenter(ApplicationContext ctx, CachingDispatcherAsync dispatcher, EventBus eventBus, PlaceController placeController) {
		this.ctx = ctx;
		this.dispatcher = dispatcher;
		this.eventBus = eventBus;
		this.placeController = placeController;
  }
}
