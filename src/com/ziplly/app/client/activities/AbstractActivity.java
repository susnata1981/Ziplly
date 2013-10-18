package com.ziplly.app.client.activities;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;

public abstract class AbstractActivity implements Activity {
	protected CachingDispatcherAsync dispatcher;
	protected PlaceController placeController;
	protected EventBus eventBus;

	public AbstractActivity(CachingDispatcherAsync dispatcher, EventBus eventBus, PlaceController placeController) {
		this.dispatcher = dispatcher;
		this.eventBus = eventBus;
		this.placeController = placeController;
	}

	public void goTo(Place place) {
		placeController.goTo(place);
	}
	
	@Override
	public String mayStop() {
		return null;
	}

	@Override
	public void onCancel() {
	}

	@Override
	public void onStop() {
	}
}
