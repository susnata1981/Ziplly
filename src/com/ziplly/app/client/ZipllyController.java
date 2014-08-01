package com.ziplly.app.client;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.places.OAuthPlace;
import com.ziplly.app.client.places.SignupPlace;
import com.ziplly.app.client.view.NavView;

public class ZipllyController {
	EventBus eventBus;

	// main content
	ActivityMapper activityMapper;
	ActivityManager activityManager;

	// nav area
	ActivityMapper navActivityMapper;
	ActivityManager navActivityManager;

	PlaceHistoryMapper placeHistoryMapper;

	PlaceHistoryHandler historyHandler;
	PlaceController placeController;

	Place defaultPlace;
	RootPanel container;
	SimplePanel panel = new SimplePanel();
	SimplePanel navPanel = new SimplePanel();
	NavView navView;

	CachingDispatcherAsync dispatcher;
	com.google.gwt.event.shared.EventBus appEventBus;
	ApplicationContext ctx;

	private RootPanel navContainer;

	@Inject
	public ZipllyController(ApplicationContext ctx,
	    CachingDispatcherAsync dispatcher,
	    EventBus eventBus,
	    com.google.gwt.event.shared.EventBus appEventBus,
	    ActivityMapper activityMapper,
	    ActivityManager activityManager,
	    @Named("nav") ActivityMapper navActivityMapper,
	    @Named("nav") ActivityManager navActivityManager,
	    PlaceController placeController,
	    PlaceHistoryMapper placeHistoryMapper,
	    PlaceHistoryHandler placeHistoryHandler,
	    NavView navView) {
		this.ctx = ctx;
		this.dispatcher = dispatcher;
		this.eventBus = eventBus;
		this.appEventBus = appEventBus;
		this.container = RootPanel.get("main");
		this.container.add(panel);
		this.navContainer = RootPanel.get("nav");
		this.navContainer.add(navPanel);
		this.activityMapper = activityMapper;
		this.activityManager = activityManager;
		this.activityManager.setDisplay(panel);
		this.navActivityMapper = navActivityMapper;
		this.navActivityManager = navActivityManager;
		this.navActivityManager.setDisplay(navPanel);
		this.placeHistoryMapper = placeHistoryMapper;
		this.historyHandler = placeHistoryHandler;
		this.placeController = placeController;
		this.defaultPlace = new SignupPlace();
		this.historyHandler.register(placeController, eventBus, defaultPlace);
	}

	public static native void consolelog(String msg) /*-{
	                                                $wnd.console.log(msg);
	                          }-*/;

	public void go() {
		String code = Window.Location.getParameter("code");
		if (code != null) {
			consolelog("going to OauthPlace");
			placeController.goTo(new OAuthPlace(code));
			return;
		}
		historyHandler.handleCurrentHistory();
	}
}
