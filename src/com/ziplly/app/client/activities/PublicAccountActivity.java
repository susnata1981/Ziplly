package com.ziplly.app.client.activities;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.PublicAccountPlace;
import com.ziplly.app.client.view.AccountView;
import com.ziplly.app.client.view.BusinessAccountView;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.shared.GetAccountByIdAction;
import com.ziplly.app.shared.GetAccountByIdResult;

public class PublicAccountActivity extends AbstractActivity {
	private PublicAccountPlace place;
	private AccountView pview;
	private BusinessAccountView bview;
	private AcceptsOneWidget panel;
	
	public PublicAccountActivity(CachingDispatcherAsync dispatcher,
			EventBus eventBus, PlaceController placeController,
			ApplicationContext ctx,
			PublicAccountPlace place,
			AccountView pview,
			BusinessAccountView bview) {
		super(dispatcher, eventBus, placeController, ctx);
		this.setPlace(place);
		this.pview = pview;
		this.bview = bview;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		if(place.getAccountId() != null) {
			displayPublicProfile(place.getAccountId());
		}
	}

	public PublicAccountPlace getPlace() {
		return place;
	}

	public void setPlace(PublicAccountPlace place) {
		this.place = place;
	}
	
	protected void displayPublicProfile(Long accountId) {
		dispatcher.execute(new GetAccountByIdAction(accountId), new DispatcherCallbackAsync<GetAccountByIdResult>() {
			@Override
			public void onSuccess(GetAccountByIdResult result) {
				AccountDTO account = result.getAccount();
				if (account instanceof PersonalAccountDTO) {
					pview.displayPublicProfile((PersonalAccountDTO)account);
					panel.setWidget(pview);
				} else if (account instanceof BusinessAccountDTO) {
					bview.displayPublicProfile((BusinessAccountDTO)account);
				}
			}
		});
	}

}
