package com.ziplly.app.client.activities;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
	private AsyncProvider<SignupView> viewProvider;

	@Inject
	public SignupActivity(
			CachingDispatcherAsync dispatcher, 
			EventBus eventBus,
			PlaceController placeController, 
			SignupPlace place, 
			ApplicationContext ctx, 
			AsyncProvider<SignupView> viewProvider) {
		super(dispatcher, eventBus, placeController, ctx, null);
		this.place = place;
		this.viewProvider = viewProvider;
	}

	@Override
	public void start(final AcceptsOneWidget panel, EventBus eventBus) {
		viewProvider.get(new AsyncCallback<SignupView>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(SignupView result) {
				SignupActivity.this.view = result;
				bind();
				view.reset();
				if (place.getAccount() != null) {
					AccountDTO a = place.getAccount();
					if (a instanceof PersonalAccountDTO ) {
						view.displayAccount((PersonalAccountDTO)a);
					}
				}
				panel.setWidget(view);
			}
		});
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
	public void onStop() {
		view.clear();
		clearBackgroundImage();
	}
	
	@Override
	public void fetchData() {
	}

}
