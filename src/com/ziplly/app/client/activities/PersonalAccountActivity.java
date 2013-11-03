package com.ziplly.app.client.activities;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.view.AccountView;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.view.handler.LoginEventHandler;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.shared.GetAccountByIdAction;
import com.ziplly.app.shared.GetAccountByIdResult;

public class PersonalAccountActivity extends
		AbstractAccountActivity<PersonalAccountDTO> {
	private PersonalAccountPlace place;
	private AcceptsOneWidget panel;

	@Inject
	public PersonalAccountActivity(CachingDispatcherAsync dispatcher,
			EventBus eventBus, PlaceController placeController,
			ApplicationContext ctx, AccountView view, PersonalAccountPlace place) {
		super(dispatcher, eventBus, placeController, ctx, view);
		System.out
				.println("Creating new instace of personal account activity...");
		this.place = place;
		eventBus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {
			@Override
			public void onEvent(LoginEvent event) {
				System.out.println("User " + event.getAccount() + " logged in");
			}
		});
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
		System.out.println("Calling personal account activity start..."
				+ place.getAccountId());
		this.panel = panel;
		bind();
		if (place.getAccountId() != null) {
			displayProfile(place.getAccountId());
			return;
		}

		if (ctx.getAccount() != null) {
			displayProfile();
			go(panel);
		} else {
			goTo(new LoginPlace());
		}
	}

	/*
	 * Display people's profile
	 */
	void displayProfile(Long accountId) {
		dispatcher.execute(new GetAccountByIdAction(accountId),
				new DispatcherCallbackAsync<GetAccountByIdResult>() {
					@Override
					public void onSuccess(GetAccountByIdResult result) {
						AccountDTO account = result.getAccount();
						view.displayPublicProfile((PersonalAccountDTO) account);
						go(panel);
					}
				});
	}

	@Override
	public void displayPublicProfile() {
		view.displayPublicProfile((PersonalAccountDTO) ctx.getAccount());
	}

	@Override
	public void displayProfile() {
		view.displayProfile((PersonalAccountDTO) ctx.getAccount());
	}
}
