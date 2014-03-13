package com.ziplly.app.client.activities;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.places.BusinessSignupPlace;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.view.BusinessSignupView;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.model.AccountDTO;

public class BusinessSignupActivity extends AbstractSignupActivity implements
    SignupActivityPresenter {
	AcceptsOneWidget panel;
	private BusinessSignupPlace place;
	private AsyncProvider<BusinessSignupView> viewProvider;

	@Inject
	public BusinessSignupActivity(CachingDispatcherAsync dispatcher,
	    EventBus eventBus,
	    PlaceController placeController,
	    ApplicationContext ctx,
	    BusinessSignupPlace place,
	    AsyncProvider<BusinessSignupView> viewProvider) {
		super(dispatcher, eventBus, placeController, ctx, null);
		this.viewProvider = viewProvider;
		this.place = place;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		checkAccountLogin();
	}

	@Override
	public void doStart() {
		placeController.goTo(new HomePlace());
	}

	@Override
	protected void doStartOnUserNotLoggedIn() {
		viewProvider.get(new DefaultViewLoaderAsyncCallback<BusinessSignupView>() {

			@Override
			public void onSuccess(BusinessSignupView result) {
				BusinessSignupActivity.this.view = result;
				bind();
				view.reset();
				go(BusinessSignupActivity.this.panel);
			}
		});
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
		StyleHelper.clearBackground();
	}
}
