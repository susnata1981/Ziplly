package com.ziplly.app.client.activities;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.AccountPlace;
import com.ziplly.app.client.places.OAuthPlace;
import com.ziplly.app.client.places.SignupPlace;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.shared.GetFacebookDetailsAction;
import com.ziplly.app.shared.GetFacebookDetailsResult;

public class OAuthActivity extends AbstractActivity {
	private OAuthPlace place;

	@Inject
	public OAuthActivity(CachingDispatcherAsync dispatcher, EventBus eventBus,
			OAuthPlace place, PlaceController placeController) {
		super(dispatcher, eventBus, placeController);
		this.place = place;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		dispatcher.execute(new GetFacebookDetailsAction(place.getCode()), new DispatcherCallbackAsync<GetFacebookDetailsResult>() {
			@Override
			public void onSuccess(GetFacebookDetailsResult result) {
				AccountDTO account = result.getAccount();
				if (account != null) {
					if (account.getAccountId() != null) {
						// found user
						goTo(new AccountPlace());
						
					} else {
						// sign up page
						goTo(new SignupPlace(account));
					}
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				System.out.println("Exception caught in OAuthActivity: "+caught.getMessage());
			}
		});
	}

}
