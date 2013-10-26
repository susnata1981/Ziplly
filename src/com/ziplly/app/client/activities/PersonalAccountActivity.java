package com.ziplly.app.client.activities;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.view.AccountView;
import com.ziplly.app.model.PersonalAccountDTO;

public class PersonalAccountActivity extends AbstractAccountActivity<PersonalAccountDTO> {

	@Inject
	public PersonalAccountActivity(CachingDispatcherAsync dispatcher,
			EventBus eventBus, PlaceController placeController,
			ApplicationContext ctx,
			AccountView view) {
		super(dispatcher, eventBus, placeController, ctx, view);
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
		if (ctx.getAccount() != null) {
			bind();
			displayProfile();
			go(panel);
		} else{
			goTo(new LoginPlace());
		}
	}

	@Override
	public void displayPublicProfile() {
		view.displayPublicProfile((PersonalAccountDTO)ctx.getAccount());
	}

	@Override
	public void displayProfile() {
		view.displayProfile((PersonalAccountDTO)ctx.getAccount());
	}
}
