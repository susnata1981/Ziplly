package com.ziplly.app.client.activities;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.view.BusinessSignupView;

public class BusinessSignupActivity extends AbstractSignupActivity implements SignupActivityPresenter {
	private AcceptsOneWidget panel;

	@Inject
	public BusinessSignupActivity(CachingDispatcherAsync dispatcher,
			EventBus eventBus, PlaceController placeController,
			ApplicationContext ctx,
			BusinessSignupView view) {
		super(dispatcher, eventBus, placeController, ctx, view);
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		bind();
//		view.hideProfileImagePreview();
		view.reset();
		setImageUploadUrl();
		setUploadImageHandler();
		go(panel);
	}

	@Override
	public void bind() {
		view.setPresenter(this);
	}

	@Override
	public void fetchData() {
	}

	@Override
	public void go(AcceptsOneWidget container) {
		container.setWidget(view);
	}

	@Override
	public void onFacebookLogin() {
	}
	
	@Override
	public void onStop() {
		view.clear();
	}
	
}
