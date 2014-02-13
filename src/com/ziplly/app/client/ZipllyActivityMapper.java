package com.ziplly.app.client;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.ziplly.app.client.activities.AboutActivity;
import com.ziplly.app.client.activities.AdminActivity;
import com.ziplly.app.client.activities.BusinessAccountActivity;
import com.ziplly.app.client.activities.BusinessAccountSettingsActivity;
import com.ziplly.app.client.activities.BusinessActivity;
import com.ziplly.app.client.activities.BusinessSignupActivity;
import com.ziplly.app.client.activities.ConversationActivity;
import com.ziplly.app.client.activities.HomeActivity;
import com.ziplly.app.client.activities.LoginActivity;
import com.ziplly.app.client.activities.OAuthActivity;
import com.ziplly.app.client.activities.PasswordRecoveryActivity;
import com.ziplly.app.client.activities.PersonalAccountActivity;
import com.ziplly.app.client.activities.PersonalAccountSettingsActivity;
import com.ziplly.app.client.activities.ResidentActivity;
import com.ziplly.app.client.activities.SignupActivity;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.places.AboutPlace;
import com.ziplly.app.client.places.AdminPlace;
import com.ziplly.app.client.places.BusinessAccountPlace;
import com.ziplly.app.client.places.BusinessAccountSettingsPlace;
import com.ziplly.app.client.places.BusinessPlace;
import com.ziplly.app.client.places.BusinessSignupPlace;
import com.ziplly.app.client.places.ConversationPlace;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.places.OAuthPlace;
import com.ziplly.app.client.places.PasswordRecoveryPlace;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.places.PersonalAccountSettingsPlace;
import com.ziplly.app.client.places.ResidentPlace;
import com.ziplly.app.client.places.SignupPlace;
import com.ziplly.app.client.view.AboutView;
import com.ziplly.app.client.view.AccountView;
import com.ziplly.app.client.view.AdminView;
import com.ziplly.app.client.view.BusinessAccountSettingsView;
import com.ziplly.app.client.view.BusinessAccountView;
import com.ziplly.app.client.view.BusinessSignupView;
import com.ziplly.app.client.view.BusinessView;
import com.ziplly.app.client.view.ConversationView;
import com.ziplly.app.client.view.HomeView;
import com.ziplly.app.client.view.LoginAccountView;
import com.ziplly.app.client.view.PasswordRecoveryView;
import com.ziplly.app.client.view.PersonalAccountSettingsView;
import com.ziplly.app.client.view.ResidentsView;
import com.ziplly.app.client.view.SignupView;

public class ZipllyActivityMapper implements ActivityMapper {
	private PlaceController placeController;
	private CachingDispatcherAsync dispatcher;
	private EventBus eventBus;
	private ApplicationContext ctx;
	private AsyncProvider<HomeView> homeView;
	private AsyncProvider<LoginAccountView> loginAccountView;
	private AsyncProvider<AccountView> accountView;
	private AsyncProvider<SignupView> signupView;
	private AsyncProvider<BusinessSignupView> businessSignupView;
	private AsyncProvider<BusinessAccountView> businessAccountView;
	private AsyncProvider<ResidentsView> residentsView;
	private AsyncProvider<BusinessView> businessView;
	private AsyncProvider<PersonalAccountSettingsView> personalAccountSettingsView;
	private AsyncProvider<BusinessAccountSettingsView> businessAccountSettingsView;
	private AsyncProvider<ConversationView> conversationView;
	private AsyncProvider<PasswordRecoveryView> passwordRecoveryView;
	private AsyncProvider<AdminView> adminView;
	private AsyncProvider<AboutView> aboutView;
	
	@Inject
	public ZipllyActivityMapper(
			AsyncProvider<HomeView> homeView,
			AsyncProvider<LoginAccountView> loginAccountView,
			AsyncProvider<AccountView> accountView,
			AsyncProvider<BusinessAccountView> businessAccountView,
			AsyncProvider<SignupView> signupView,
			AsyncProvider<BusinessSignupView> businessSignupView,
			AsyncProvider<ResidentsView> residentsView,
			AsyncProvider<BusinessView> businessView,
			AsyncProvider<PersonalAccountSettingsView> personalAccountSettingsView,
			AsyncProvider<BusinessAccountSettingsView> businessAccountSettingsView,
			AsyncProvider<ConversationView> conversationView,
			AsyncProvider<PasswordRecoveryView> passwordRecoverView,
			AsyncProvider<AdminView> adminView,
			AsyncProvider<AboutView> aboutView,
			CachingDispatcherAsync dispatcher,
			EventBus eventBus,
			PlaceController placeController,
			ApplicationContext ctx) {
		
		this.homeView = homeView;
		this.loginAccountView = loginAccountView;
		this.accountView = accountView;
		this.businessAccountView = businessAccountView;
		this.signupView = signupView;
		this.businessSignupView = businessSignupView;
		this.residentsView = residentsView;
		this.businessView = businessView;
		this.personalAccountSettingsView = personalAccountSettingsView;
		this.businessAccountSettingsView = businessAccountSettingsView;
		this.conversationView = conversationView;
		this.passwordRecoveryView = passwordRecoverView;
		this.adminView = adminView;
		this.aboutView = aboutView;
		this.dispatcher = dispatcher;
		this.eventBus = eventBus;
		this.placeController = placeController;
		this.ctx = ctx;
	}
	
	@Override
	public Activity getActivity(final Place place) {
		if (place instanceof HomePlace) {
			return new HomeActivity(dispatcher, eventBus, (HomePlace) place, placeController, ctx, homeView);
		} 
		else if (place instanceof LoginPlace) {
			return new LoginActivity(dispatcher, eventBus, (LoginPlace)place, placeController, ctx, loginAccountView);
		} 
		else if (place instanceof SignupPlace) {
			return new SignupActivity(dispatcher, eventBus, placeController, (SignupPlace)place, ctx, signupView);
		}
		else if (place instanceof BusinessSignupPlace) {
			return new BusinessSignupActivity(dispatcher, eventBus, placeController, ctx, (BusinessSignupPlace)place, businessSignupView);
		}
		else if (place instanceof OAuthPlace) {
			return new OAuthActivity(dispatcher, eventBus, placeController, ctx, (OAuthPlace)place);
		}
		else if (place instanceof PersonalAccountPlace) {
			return new PersonalAccountActivity(dispatcher, eventBus, placeController, ctx, accountView, (PersonalAccountPlace)place);
		}
		else if (place instanceof BusinessAccountPlace) {
			return new BusinessAccountActivity(dispatcher, eventBus, placeController, ctx, businessAccountView, (BusinessAccountPlace)place);
		}
		else if (place instanceof PersonalAccountSettingsPlace) {
			return new PersonalAccountSettingsActivity(dispatcher, eventBus, placeController, ctx, personalAccountSettingsView);
		}
		else if (place instanceof BusinessAccountSettingsPlace) {
			return new BusinessAccountSettingsActivity(dispatcher, eventBus, placeController, ctx, businessAccountSettingsView);
		}
		else if (place instanceof ConversationPlace) {
//			return new ConversationActivity(dispatcher, eventBus, placeController, ctx, (ConversationPlace)place, conversationView);
			return new ActivityProxy<ConversationActivity>(new AsyncProvider<ConversationActivity>() {

				@Override
				public void get(AsyncCallback<? super ConversationActivity> callback) {
					callback.onSuccess(new ConversationActivity(dispatcher, eventBus, placeController, ctx, (ConversationPlace)place, conversationView));
				}
			});
		}
		else if (place instanceof ResidentPlace) {
//			return new ResidentActivity(dispatcher, eventBus, placeController, ctx, ((ResidentPlace)place), residentsView);
			return new ActivityProxy<ResidentActivity>(new AsyncProvider<ResidentActivity>() {

				@Override
				public void get(AsyncCallback<? super ResidentActivity> callback) {
					callback.onSuccess(new ResidentActivity(dispatcher, eventBus, placeController, ctx, ((ResidentPlace)place), residentsView));
				}
			});
		}
		else if (place instanceof BusinessPlace) {
			return new BusinessActivity(dispatcher, eventBus, placeController, ctx, ((BusinessPlace)place), businessView); 
		}
		else if (place instanceof PasswordRecoveryPlace) {
			return new PasswordRecoveryActivity(dispatcher, eventBus, placeController, ctx, (PasswordRecoveryPlace)place, passwordRecoveryView);
		}
		else if (place instanceof AdminPlace) {
			return new AdminActivity(dispatcher, eventBus, placeController, ctx, adminView);
		}
		else if (place instanceof AboutPlace) {
			return new AboutActivity(dispatcher, eventBus, placeController, ctx, (AboutPlace)place, aboutView);
		}
		
		throw new IllegalArgumentException();
	}
}
