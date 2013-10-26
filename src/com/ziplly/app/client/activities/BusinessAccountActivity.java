package com.ziplly.app.client.activities;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.BusinessAccountPlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.view.IAccountView;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.shared.GetAccountByIdAction;
import com.ziplly.app.shared.GetAccountByIdResult;

public class BusinessAccountActivity extends AbstractAccountActivity<BusinessAccountDTO> {

	private BusinessAccountPlace place;

	public BusinessAccountActivity(CachingDispatcherAsync dispatcher,
			EventBus eventBus, PlaceController placeController,
			ApplicationContext ctx, IAccountView<BusinessAccountDTO> view, BusinessAccountPlace place) {
		super(dispatcher, eventBus, placeController, ctx, view);
		this.place = place;
	}

	@Override
	public void bind() {
		view.setPresenter(this);
	}
	
	@Override
	public void go(AcceptsOneWidget container) {
		container.setWidget(view);
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		
		if (place.getAccountId() != null) {
			displayPublicProfile(place.getAccountId());
		} else if (ctx.getAccount() != null) {
			bind();
			setImageUploadUrl();
			setUploadImageHandler();
			displayProfile();
			go(panel);
		} else{
			goTo(new LoginPlace());
		}
	}

	@Override
	public void displayProfile() {
		view.displayProfile((BusinessAccountDTO)ctx.getAccount());
	}

	@Override
	public void displayPublicProfile() {
		
	}
}
