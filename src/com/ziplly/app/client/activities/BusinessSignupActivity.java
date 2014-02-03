package com.ziplly.app.client.activities;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.places.BusinessSignupPlace;
import com.ziplly.app.client.view.BusinessSignupView;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.model.AccountDTO;

public class BusinessSignupActivity extends AbstractSignupActivity implements
		SignupActivityPresenter {
	AcceptsOneWidget panel;
	private BusinessSignupPlace place;

	@Inject
	public BusinessSignupActivity(CachingDispatcherAsync dispatcher, EventBus eventBus,
			PlaceController placeController, ApplicationContext ctx, BusinessSignupPlace place,
			BusinessSignupView view) {
		super(dispatcher, eventBus, placeController, ctx, view);
		this.place = place;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		bind();
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
	public void register(AccountDTO account, String code) {
		if (account != null) {
			try {
				Integer signupCode = Integer.parseInt(place.getCode());
				verifyInvitationForEmail(account, signupCode);
			} catch (NumberFormatException nf) {
				view.displayMessage(StringConstants.INVALID_ACCESS, AlertType.ERROR);
				return;
			}
		} else {
			view.displayMessage(StringConstants.NEEDS_INVITATION, AlertType.ERROR);
		}
	}

	@Override
	public void go(AcceptsOneWidget container) {
		container.setWidget(view);
	}

	@Override
	public void onStop() {
		view.clear();
	}

	@Override
	public void onFacebookLogin() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void fetchData() {
	}
}
