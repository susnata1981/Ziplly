package com.ziplly.app.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Provider;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.google.web.bindery.event.shared.EventBus;
import com.ziplly.app.client.activities.BusinessAccountSettingsActivity.IBusinessAccountSettingView;
import com.ziplly.app.client.activities.CouponReportActivity.CouponReportView;
import com.ziplly.app.client.activities.HomeActivity.HomeView;
import com.ziplly.app.client.activities.NavActivity.INavView;
import com.ziplly.app.client.conversation.ConversationView;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.exceptions.GlobalErrorHandler;
import com.ziplly.app.client.places.BusinessAccountPlace;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.places.PlaceParser;
import com.ziplly.app.client.places.PlaceParserImpl;
import com.ziplly.app.client.places.SignupPlace;
import com.ziplly.app.client.view.AboutView;
import com.ziplly.app.client.view.AccountView;
import com.ziplly.app.client.view.BusinessAccountSettingsView;
import com.ziplly.app.client.view.BusinessAccountView;
import com.ziplly.app.client.view.EmailVerificationView;
import com.ziplly.app.client.view.HomeViewImpl;
import com.ziplly.app.client.view.IAccountView;
import com.ziplly.app.client.view.ILoginAccountView;
import com.ziplly.app.client.view.ISignupView;
import com.ziplly.app.client.view.LoginAccountView;
import com.ziplly.app.client.view.NavView;
import com.ziplly.app.client.view.PasswordRecoveryView;
import com.ziplly.app.client.view.PersonalAccountSettingsView;
import com.ziplly.app.client.view.TweetDetailsView;
import com.ziplly.app.client.view.community.ResidentsView;
import com.ziplly.app.client.view.coupon.CouponReportViewImpl;
import com.ziplly.app.client.view.signup.BusinessSignupView;
import com.ziplly.app.client.view.signup.SignupView;
import com.ziplly.app.client.widget.SendMessageWidget;

public class ZClientModule extends AbstractGinModule {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@BindingAnnotation
	public @interface TweetsPerPage {
	}
	
	@Override
	protected void configure() {
		bindConstant().annotatedWith(TweetsPerPage.class).to(10);
		bindConstant().annotatedWith(Names.named("tpp")).to(5);
		
		bind(PlaceParser.class).to(PlaceParserImpl.class).in(Singleton.class);
		bind(ApplicationContext.class).in(Singleton.class);
		bind(CachingDispatcherAsync.class).in(Singleton.class);
		bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
		bind(com.google.gwt.event.shared.EventBus.class).to(SimpleEventBus.class).in(Singleton.class);

		
		// main presenter
		bind(ZipllyController.class).in(Singleton.class);

		// views
		bind(INavView.class).to(NavView.class).in(Singleton.class);
		bind(IAccountView.class).to(AccountView.class).in(Singleton.class);
		bind(BusinessAccountView.class);
		bind(ILoginAccountView.class).to(LoginAccountView.class).in(Singleton.class);
		bind(ISignupView.class).to(BusinessSignupView.class).in(Singleton.class);
		bind(HomeView.class).to(HomeViewImpl.class).in(Singleton.class);
		bind(TweetDetailsView.class).in(Singleton.class);
		bind(SignupView.class).in(Singleton.class);
		bind(BusinessSignupView.class).in(Singleton.class);
		bind(ResidentsView.class).in(Singleton.class);
		bind(PersonalAccountSettingsView.class);
		bind(IBusinessAccountSettingView.class).to(BusinessAccountSettingsView.class);
		bind(ConversationView.class);
		bind(PasswordRecoveryView.class);
		bind(EmailVerificationView.class);
		bind(AboutView.class);
		bind(CouponReportView.class).to(CouponReportViewImpl.class).in(Singleton.class);
		
		// places
		bind(HomePlace.class);
		bind(LoginPlace.class);
		bind(SignupPlace.class);
		bind(BusinessAccountPlace.class);

		bind(GlobalErrorHandler.class).in(Singleton.class);
		
		bind(ActivityMapper.class).to(ZipllyActivityMapper.class).in(Singleton.class);
		bind(ActivityMapper.class)
		    .annotatedWith(Names.named("nav"))
		    .to(NavActivityMapper.class)
		    .in(Singleton.class);

		bind(PlaceHistoryMapper.class).toProvider(PlaceHistoryMapperProvider.class).in(Singleton.class);
		bind(PlaceHistoryHandler.class).toProvider(PlaceHistoryHandlerProvider.class).in(
		    Singleton.class);

		bind(ActivityManager.class).toProvider(ActivityManagerProvider.class).in(Singleton.class);
		bind(ActivityManager.class)
		    .annotatedWith(Names.named("nav"))
		    .toProvider(NavActivityManagerProvider.class)
		    .in(Singleton.class);

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

	public static class SendMessageWidgetFactory implements AsyncProvider<SendMessageWidget> {

		@Override
		public void get(final AsyncCallback<? super SendMessageWidget> callback) {
			GWT.runAsync(new RunAsyncCallback() {

				@Override
				public void onSuccess() {
					callback.onSuccess(new SendMessageWidget(null));
				}

				@Override
				public void onFailure(Throwable reason) {
				}
			});
		}
	}

	public static class SendMessageWidgetProvider implements
	    Provider<AsyncProvider<SendMessageWidget>> {

		@Inject
		SendMessageWidgetFactory factory;

		@Override
		public AsyncProvider<SendMessageWidget> get() {
			return factory;
		}
	}
}
