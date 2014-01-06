package com.ziplly.app.client.activities;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.view.ResidentsView;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.view.handler.LoginEventHandler;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.EntityType;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.shared.GetEntityListAction;
import com.ziplly.app.shared.GetEntityResult;

public class ResidentActivity extends AbstractActivity implements Presenter {
	private ResidentsView view;
	private Place place;
	private AcceptsOneWidget panel;

	@Inject
	public ResidentActivity(CachingDispatcherAsync dispatcher,
			EventBus eventBus, PlaceController placeController,
			ApplicationContext ctx,
			ResidentsView view) {
		super(dispatcher, eventBus, placeController, ctx);
		this.view = view;
		setupHandlers();
	}

	private void setupHandlers() {
		eventBus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {
			@Override
			public void onEvent(LoginEvent event) {
				displayResidentList();
			}
		});
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		if (ctx.getAccount() != null) {
			displayResidentList();
		} else {
			checkLoginStatus();
			this.panel = panel;
		}
	}

	void displayResidentList() {
		dispatcher.execute(new GetEntityListAction(EntityType.PERSONAL_ACCOUNT), 
				new DispatcherCallbackAsync<GetEntityResult>() {
			@Override
			public void onSuccess(GetEntityResult result) {
				List<PersonalAccountDTO> accounts = new ArrayList<PersonalAccountDTO>();
				if (result.getEntityType() == EntityType.PERSONAL_ACCOUNT) {
					for(AccountDTO acct : result.getAccounts()) {
						accounts.add((PersonalAccountDTO)acct);
					}
				}
				view.display(accounts);
				panel.setWidget(view);
			}
			
			public void onFailure(Throwable caught) {
				// TODO
				Window.alert(caught.getLocalizedMessage());
			}
		});
	}

	@Override
	public void bind() {
	}

	@Override
	public void fetchData() {
	}

	@Override
	public void go(AcceptsOneWidget container) {
	}
}
