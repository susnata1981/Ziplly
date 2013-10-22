package com.ziplly.app.client;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.places.OAuthPlace;
import com.ziplly.app.client.view.NavView;

public class ZipllyController {
	EventBus eventBus;
	
	ActivityMapper activityMapper;	
	ActivityManager activityManager;
	PlaceHistoryMapper placeHistoryMapper;
	
	PlaceHistoryHandler historyHandler;
	PlaceController placeController;
	
	HomePlace defaultPlace;
	RootPanel container;
	SimplePanel panel = new SimplePanel();
	NavView navView;

	CachingDispatcherAsync dispatcher;
	
	@Inject
	public ZipllyController(
			CachingDispatcherAsync dispatcher,
			EventBus eventBus, 
			ActivityMapper activityMapper,
			ActivityManager activityManager,
			PlaceController placeController,
			PlaceHistoryMapper placeHistoryMapper,
			PlaceHistoryHandler placeHistoryHandler,
			NavView navView) {
		this.dispatcher = dispatcher;
		this.eventBus = eventBus;
		this.container = RootPanel.get("main");
		RootPanel.get("nav").add(navView);
		this.container.add(panel);
		this.activityMapper = activityMapper;
		this.activityManager = activityManager;			
		this.activityManager.setDisplay(panel);
		this.placeHistoryMapper = placeHistoryMapper; 		
		this.historyHandler = placeHistoryHandler; 			
		this.placeController = placeController; 		
		this.defaultPlace = new HomePlace();
		historyHandler.register(placeController, eventBus, defaultPlace);
	}
	
	public void go() {
		String code = Window.Location.getParameter("code");
		if (code!=null) {
			placeController.goTo(new OAuthPlace(code));
		}
		historyHandler.handleCurrentHistory();
	}
}
