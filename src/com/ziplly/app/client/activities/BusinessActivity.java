package com.ziplly.app.client.activities;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.BusinessPlace;
import com.ziplly.app.client.view.BusinessView;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.view.handler.LoginEventHandler;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.EntityType;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.shared.GetEntityListAction;
import com.ziplly.app.shared.GetEntityResult;

public class BusinessActivity extends AbstractActivity implements Presenter {
	private BusinessView view;
	private BusinessPlace place;
	private AcceptsOneWidget panel;

	@Inject
	public BusinessActivity(CachingDispatcherAsync dispatcher,
			EventBus eventBus, PlaceController placeController,
			ApplicationContext ctx,
			BusinessView view) {
		super(dispatcher, eventBus, placeController, ctx);
		this.view = view;
		setupHandlers();
	}

	private void setupHandlers() {
		eventBus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {
			@Override
			public void onEvent(LoginEvent event) {
				displayBusinessList();
			}
		});
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		if (ctx.getAccount() != null) {
			displayBusinessList();
		} else {
			checkLoginStatus();
			this.panel = panel;
		}
	}

	void displayBusinessList() {
		dispatcher.execute(new GetEntityListAction(EntityType.BUSINESS_ACCOUNT), 
				new DispatcherCallbackAsync<GetEntityResult>() {
			@Override
			public void onSuccess(GetEntityResult result) {
				List<BusinessAccountDTO> accounts = new ArrayList<BusinessAccountDTO>();
				if (result.getEntityType() == EntityType.BUSINESS_ACCOUNT) {
					for(AccountDTO acct : result.getAccounts()) {
						accounts.add((BusinessAccountDTO)acct);
					}
				}
				view.display(accounts);
				panel.setWidget(view);
			}
			
			public void onFailure(Throwable caught) {
				// TODO
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
