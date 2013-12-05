package com.ziplly.app.client.activities;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.places.SignupPlace;
import com.ziplly.app.client.view.SignupView;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;

public class SignupActivity extends AbstractSignupActivity {
	private SignupPlace place;

	@Inject
	public SignupActivity(CachingDispatcherAsync dispatcher, EventBus eventBus,
			PlaceController placeController, SignupPlace place, ApplicationContext ctx, SignupView view) {
		super(dispatcher, eventBus, placeController, ctx, view);
		this.place = place;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		bind();
//		view.hideProfileImagePreview();
		view.reset();
		setImageUploadUrl();
		setUploadImageHandler();
		
		if (place.getAccount() != null) {
			AccountDTO a = place.getAccount();
			if (a instanceof PersonalAccountDTO ) {
				view.displayAccount((PersonalAccountDTO)a);
			}
		}
		panel.setWidget(view);
	}

	@Override
	public void fetchData() {
	}

	@Override
	public void go(AcceptsOneWidget container) {
		container.setWidget(view);
	}

	@Override
	public void bind() {
		view.setPresenter(this);
	}

	@Override
	public void onFacebookLogin() {
	}

	@Override
	public void onStop() {
		view.clear();
	}
}
