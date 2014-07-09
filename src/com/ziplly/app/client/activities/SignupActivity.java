package com.ziplly.app.client.activities;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.places.SignupPlace;
import com.ziplly.app.client.view.signup.SignupView;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;

public class SignupActivity extends AbstractSignupActivity {
	private AcceptsOneWidget panel;
	private final SignupPlace place;
	private final AsyncProvider<SignupView> viewProvider;

	@Inject
	public SignupActivity(final CachingDispatcherAsync dispatcher,
			final EventBus eventBus,
			final PlaceController placeController,
			final SignupPlace place,
			final ApplicationContext ctx,
			final AsyncProvider<SignupView> viewProvider) {
		super(dispatcher, eventBus, placeController, ctx, null);
		this.place = place;
		this.viewProvider = viewProvider;
	}

	@Override
	public void bind() {
		view.setPresenter(this);
	}

	@Override
	public void doStart() {
		placeController.goTo(new HomePlace());
	}

	@Override
	protected void doStartOnUserNotLoggedIn() {
		viewProvider.get(new AsyncCallback<SignupView>() {

			@Override
			public void onFailure(final Throwable caught) {
			  Window.alert(stringDefinitions.pageLoadError());
			}

			@Override
			public void onSuccess(final SignupView result) {
				SignupActivity.this.view = result;
				bind();
				view.reset();
				if (place.getAccount() != null) {
					AccountDTO a = place.getAccount();
					if (a instanceof PersonalAccountDTO) {
						view.displayAccount((PersonalAccountDTO) a);
					}
				}
				panel.setWidget(view);
			}
		});
	}

	@Override
	public void go(final AcceptsOneWidget container) {
		container.setWidget(view);
	}

	@Override
	public void onStop() {
		view.clear();
		StyleHelper.clearBackground();
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		this.panel = panel;
		checkAccountLogin();
	}
}
