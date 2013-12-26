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
import com.google.inject.name.Names;
import com.google.web.bindery.event.shared.EventBus;
import com.ziplly.app.client.activities.BusinessAccountSettingsActivity.IBusinessAccountSettingView;
import com.ziplly.app.client.activities.HomeActivity;
import com.ziplly.app.client.activities.HomeActivity.IHomeView;
import com.ziplly.app.client.activities.LoginActivity;
import com.ziplly.app.client.activities.NavActivity;
import com.ziplly.app.client.activities.NavActivity.INavView;
import com.ziplly.app.client.activities.SignupActivity;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.places.BusinessAccountPlace;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.places.SignupPlace;
import com.ziplly.app.client.view.AboutView;
import com.ziplly.app.client.view.AccountView;
import com.ziplly.app.client.view.BusinessAccountSettingsView;
import com.ziplly.app.client.view.BusinessSignupView;
import com.ziplly.app.client.view.ConversationView;
import com.ziplly.app.client.view.HomeView;
import com.ziplly.app.client.view.IAccountView;
import com.ziplly.app.client.view.ILoginAccountView;
import com.ziplly.app.client.view.ISignupView;
import com.ziplly.app.client.view.LoginAccountView;
import com.ziplly.app.client.view.MainView;
import com.ziplly.app.client.view.NavView;
import com.ziplly.app.client.view.PasswordRecoveryView;
import com.ziplly.app.client.view.PersonalAccountSettingsView;
import com.ziplly.app.client.view.ResidentsView;
import com.ziplly.app.client.view.SignupView;
import com.ziplly.app.client.widget.EditAccount;
import com.ziplly.app.client.widget.EditBusinessAccountWidget;
import com.ziplly.app.client.widget.LoginWidget;
import com.ziplly.app.client.widget.LogoutWidget;
import com.ziplly.app.client.widget.dataprovider.BasicAccountDataProvider;

public class ZClientModule extends AbstractGinModule {

	@Override
	protected void configure() {
		bind(ApplicationContext.class).in(Singleton.class);
		bind(CachingDispatcherAsync.class).in(Singleton.class);
		bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
		bind(com.google.gwt.event.shared.EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
		bind(BasicAccountDataProvider.class);
		
		// main presenter
		bind(MainController.class).in(Singleton.class);
		bind(ZipllyController.class).in(Singleton.class);
		
		// views
		bind(INavView.class).to(NavView.class).in(Singleton.class);
		bind(IAccountView.class).to(AccountView.class).in(Singleton.class);
		bind(ILoginAccountView.class).to(LoginAccountView.class).in(Singleton.class);
		bind(ISignupView.class).to(BusinessSignupView.class).in(Singleton.class);
		bind(IHomeView.class).to(HomeView.class).in(Singleton.class);
		bind(SignupView.class).in(Singleton.class);
		bind(BusinessSignupView.class).in(Singleton.class);
		bind(MainView.class).in(Singleton.class);
		bind(ResidentsView.class).in(Singleton.class);
		bind(PersonalAccountSettingsView.class);
		bind(IBusinessAccountSettingView.class).to(BusinessAccountSettingsView.class);
		bind(ConversationView.class);
		bind(PasswordRecoveryView.class);
		bind(AboutView.class);
		
		// widgets
		bind(LoginWidget.class).in(Singleton.class);
		bind(LogoutWidget.class).in(Singleton.class);
		bind(EditAccount.class).annotatedWith(Names.named("business")).to(EditBusinessAccountWidget.class).in(Singleton.class);
		
		// activities
		bind(HomeActivity.class);
		bind(LoginActivity.class);
		bind(SignupActivity.class);
		bind(NavActivity.class);
		
		// places
		bind(HomePlace.class);
		bind(LoginPlace.class);
		bind(SignupPlace.class);
		bind(BusinessAccountPlace.class);
		
		bind(ActivityMapper.class).to(ZipllyActivityMapper.class).in(Singleton.class);
		bind(ActivityMapper.class).annotatedWith(Names.named("nav")).to(NavActivityMapper.class).in(Singleton.class);
		bind(PlaceHistoryMapper.class).toProvider(PlaceHistoryMapperProvider.class).in(Singleton.class);
		bind(PlaceHistoryHandler.class).toProvider(PlaceHistoryHandlerProvider.class).in(Singleton.class);
		bind(ActivityManager.class).toProvider(ActivityManagerProvider.class).in(Singleton.class);
		bind(ActivityManager.class).annotatedWith(Names.named("nav")).toProvider(NavActivityManagerProvider.class).in(Singleton.class);
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
	
	public static class NavActivityManagerProvider implements Provider<ActivityManager> {
		EventBus eventBus;
		NavActivityMapper mapper;
		@Inject
		public NavActivityManagerProvider(NavActivityMapper am, EventBus eventBus) {
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
