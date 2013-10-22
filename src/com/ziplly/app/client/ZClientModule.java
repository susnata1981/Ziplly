package com.ziplly.app.client;

import javax.inject.Provider;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.ziplly.app.client.activities.AccountActivity;
import com.ziplly.app.client.activities.HomeActivity;
import com.ziplly.app.client.activities.SignupActivity;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.places.AccountPlace;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.places.SignupPlace;
import com.ziplly.app.client.view.AccountView;
import com.ziplly.app.client.view.HomeView;
import com.ziplly.app.client.view.IHomeView;
import com.ziplly.app.client.view.MainView;
import com.ziplly.app.client.view.NavView;
import com.ziplly.app.client.view.SignupView;
import com.ziplly.app.client.widget.LoginWidget;
import com.ziplly.app.client.widget.LogoutWidget;

public class ZClientModule extends AbstractGinModule {

	@Override
	protected void configure() {
		bind(CachingDispatcherAsync.class).in(Singleton.class);
		bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
		bind(com.google.gwt.event.shared.EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
		
		// main presenter
		bind(MainController.class).in(Singleton.class);
		bind(ZipllyController.class).in(Singleton.class);
		
		// views
		bind(AccountView.class).in(Singleton.class);
		bind(IHomeView.class).to(HomeView.class).in(Singleton.class);
		bind(SignupView.class).in(Singleton.class);
		bind(MainView.class).in(Singleton.class);
		bind(NavView.class).in(Singleton.class);
		
		// widgets
		bind(LoginWidget.class).in(Singleton.class);
		bind(LogoutWidget.class).in(Singleton.class);
		
		// activities
		bind(HomeActivity.class);
		bind(AccountActivity.class);
		bind(SignupActivity.class);
		
		// places
		bind(HomePlace.class);
		bind(AccountPlace.class);
		bind(SignupPlace.class);
		
		bind(ActivityMapper.class).to(ZipllyActivityMapper.class).in(Singleton.class);
		bind(PlaceHistoryMapper.class).toProvider(PlaceHistoryMapperProvider.class).in(Singleton.class);
		bind(PlaceHistoryHandler.class).toProvider(PlaceHistoryHandlerProvider.class).in(Singleton.class);
		bind(ActivityManager.class).toProvider(ActivityManagerProvider.class).in(Singleton.class);
		bind(PlaceController.class).toProvider(PlaceControllerProvider.class).in(Singleton.class);
	}
	
	public static class PlaceControllerProvider implements Provider<PlaceController> {
		EventBus eventBus;
		@Inject
		public PlaceControllerProvider(EventBus eventBus) {
			this.eventBus = eventBus;
		}
		@Override
		public PlaceController get() {
			return new PlaceController(eventBus);
		}
	}
	
	public static class PlaceHistoryHandlerProvider implements Provider<PlaceHistoryHandler> {
		PlaceHistoryMapper placeHistoryMapper;
		@Inject
		public PlaceHistoryHandlerProvider(PlaceHistoryMapper placeHistoryMapper) {
			this.placeHistoryMapper = placeHistoryMapper;
		}
		@Override
		public PlaceHistoryHandler get() {
			PlaceHistoryHandler handler = new PlaceHistoryHandler(placeHistoryMapper);
			return handler;
		}
	}
	
	public static class PlaceHistoryMapperProvider implements Provider<ZipllyPlaceHistoryMapper> {
		@Override
		public ZipllyPlaceHistoryMapper get() {
			return GWT.create(ZipllyPlaceHistoryMapper.class);
		}
	}
	
	public static class ActivityManagerProvider implements Provider<ActivityManager> {
		EventBus eventBus;
		ActivityMapper mapper;
		@Inject
		public ActivityManagerProvider(ActivityMapper am, EventBus eventBus) {
			this.mapper = am;
			this.eventBus = eventBus;
		}
		@Override
		public ActivityManager get() {
			ActivityManager manager = new ActivityManager(mapper, eventBus);
			return manager;
		}
	}
}
