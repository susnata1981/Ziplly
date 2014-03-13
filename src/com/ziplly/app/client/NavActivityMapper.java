package com.ziplly.app.client;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.ziplly.app.client.activities.NavActivity;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.view.NavView;

public class NavActivityMapper implements ActivityMapper {
	private NavView view;
	private PlaceController placeController;
	private EventBus eventBus;
	private CachingDispatcherAsync dispatcher;
	private ApplicationContext ctx;

	@Inject
	public NavActivityMapper(NavView view,
	    CachingDispatcherAsync dispatcher,
	    EventBus eventBus,
	    PlaceController placeController,
	    ApplicationContext ctx) {

		this.dispatcher = dispatcher;
		this.eventBus = eventBus;
		this.placeController = placeController;
		this.ctx = ctx;
		this.view = view;
	}

	@Override
	public Activity getActivity(Place place) {
		// always
		return new NavActivity(dispatcher, eventBus, placeController, ctx, view);
	}

}
