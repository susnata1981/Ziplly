package com.ziplly.app.client;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.ziplly.app.client.activities.AccountActivity;
import com.ziplly.app.client.activities.HomeActivity;
import com.ziplly.app.client.activities.OAuthActivity;
import com.ziplly.app.client.activities.SignupActivity;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.places.AccountPlace;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.places.OAuthPlace;
import com.ziplly.app.client.places.SignupPlace;
import com.ziplly.app.client.view.AccountView;
import com.ziplly.app.client.view.HomeView;
import com.ziplly.app.client.view.MainView;
import com.ziplly.app.client.view.SignupView;

public class ZipllyActivityMapper implements ActivityMapper{
	MainView mainView;
	HomeView homeView;
	AccountView accountView;
	SignupView signupView;
	private PlaceController placeController;
	private CachingDispatcherAsync dispatcher;
	private EventBus eventBus;
	
	@Inject
	public ZipllyActivityMapper(
			MainView mainView,
			HomeView homeView,
			AccountView accountView,
			SignupView signupView,
			CachingDispatcherAsync dispatcher,
			EventBus eventBus,
			PlaceController placeController) {
		
		this.mainView = mainView;
		this.homeView = homeView;
		this.accountView = accountView;
		this.signupView = signupView;
		this.dispatcher = dispatcher;
		this.eventBus = eventBus;
		this.placeController = placeController;
	}
	
	@Override
	public Activity getActivity(Place place) {
		if (place instanceof HomePlace) {
			return new HomeActivity(dispatcher, eventBus, (HomePlace) place, placeController, mainView, homeView);
		} 
		else if (place instanceof AccountPlace) {
			return new AccountActivity(dispatcher, eventBus, (AccountPlace)place, placeController, accountView);
		} 
		else if (place instanceof SignupPlace) {
			return new SignupActivity(dispatcher, eventBus, placeController, (SignupPlace)place, signupView);
		}
		else if (place instanceof OAuthPlace) {
			return new OAuthActivity(dispatcher, eventBus, (OAuthPlace)place, placeController);
		}
		
		return new HomeActivity(dispatcher, eventBus, (HomePlace) place, placeController, mainView, homeView);
	}

}
