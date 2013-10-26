package com.ziplly.app.client;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.ziplly.app.client.activities.BusinessAccountActivity;
import com.ziplly.app.client.activities.BusinessSignupActivity;
import com.ziplly.app.client.activities.HomeActivity;
import com.ziplly.app.client.activities.LoginActivity;
import com.ziplly.app.client.activities.OAuthActivity;
import com.ziplly.app.client.activities.PersonalAccountActivity;
import com.ziplly.app.client.activities.PublicAccountActivity;
import com.ziplly.app.client.activities.SignupActivity;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.places.BusinessAccountPlace;
import com.ziplly.app.client.places.BusinessSignupPlace;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.places.OAuthPlace;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.places.PublicAccountPlace;
import com.ziplly.app.client.places.SignupPlace;
import com.ziplly.app.client.view.AccountView;
import com.ziplly.app.client.view.BusinessAccountView;
import com.ziplly.app.client.view.BusinessSignupView;
import com.ziplly.app.client.view.HomeView;
import com.ziplly.app.client.view.LoginAccountView;
import com.ziplly.app.client.view.MainView;
import com.ziplly.app.client.view.SignupView;

public class ZipllyActivityMapper implements ActivityMapper{
	MainView mainView;
	HomeView homeView;
	LoginAccountView loginAccountView;
	AccountView accountView;
	SignupView signupView;
	BusinessSignupView businessSignupView;
	private PlaceController placeController;
	private CachingDispatcherAsync dispatcher;
	private EventBus eventBus;
	private ApplicationContext ctx;
	private BusinessAccountView businessAccountView;
	
	@Inject
	public ZipllyActivityMapper(
			MainView mainView,
			HomeView homeView,
			LoginAccountView loginAccountView,
			AccountView accountView,
			BusinessAccountView businessAccountView,
			SignupView signupView,
			BusinessSignupView businessSignupView,
			CachingDispatcherAsync dispatcher,
			EventBus eventBus,
			PlaceController placeController,
			ApplicationContext ctx) {
		
		this.mainView = mainView;
		this.homeView = homeView;
		this.loginAccountView = loginAccountView;
		this.accountView = accountView;
		this.businessAccountView = businessAccountView;
		this.signupView = signupView;
		this.businessSignupView = businessSignupView;
		this.dispatcher = dispatcher;
		this.eventBus = eventBus;
		this.placeController = placeController;
		this.ctx = ctx;
	}
	
	@Override
	public Activity getActivity(Place place) {
		if (place instanceof HomePlace) {
			return new HomeActivity(dispatcher, eventBus, (HomePlace) place, placeController, mainView, ctx, homeView);
		} 
		else if (place instanceof LoginPlace) {
			return new LoginActivity(dispatcher, eventBus, (LoginPlace)place, placeController, ctx, loginAccountView);
		} 
		else if (place instanceof SignupPlace) {
			return new SignupActivity(dispatcher, eventBus, placeController, (SignupPlace)place, ctx, signupView);
		}
		else if (place instanceof OAuthPlace) {
			return new OAuthActivity(dispatcher, eventBus, placeController, ctx, (OAuthPlace)place);
		}
		else if (place instanceof BusinessSignupPlace) {
			return new BusinessSignupActivity(dispatcher, eventBus, placeController, ctx, businessSignupView);
		}
		else if (place instanceof PersonalAccountPlace) {
			return new PersonalAccountActivity(dispatcher, eventBus, placeController, ctx, accountView);
		}
		else if (place instanceof BusinessAccountPlace) {
			return new BusinessAccountActivity(dispatcher, eventBus, placeController, ctx, businessAccountView, (BusinessAccountPlace)place);
		}
		else if (place instanceof PublicAccountPlace) {
			return new PublicAccountActivity(dispatcher, eventBus, placeController, ctx, (PublicAccountPlace)place, accountView, businessAccountView);
		}
		return new HomeActivity(dispatcher, eventBus, (HomePlace) place, placeController, mainView, ctx, homeView);
	}

}
