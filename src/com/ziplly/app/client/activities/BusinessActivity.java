package com.ziplly.app.client.activities;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.BusinessPlace;
import com.ziplly.app.client.view.BusinessView;
import com.ziplly.app.client.view.BusinessView.EntityListViewPresenter;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.view.handler.LoginEventHandler;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.EntityType;
import com.ziplly.app.shared.GetEntityListAction;
import com.ziplly.app.shared.GetEntityResult;

public class BusinessActivity extends AbstractActivity implements EntityListViewPresenter {
	private BusinessView view;
	private BusinessPlace place;
	private EntityListHandler handler = new EntityListHandler();
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
		bind();
		if (ctx.getAccount() != null) {
			displayBusinessList();
		} else {
			checkLoginStatus();
			this.panel = panel;
		}
	}

	void displayBusinessList() {
		GetEntityListAction action = new GetEntityListAction(EntityType.BUSINESS_ACCOUNT);
		action.setPage(0);
		action.setPageSize(view.getPageSize());
		action.setNeedTotalEntityCount(true);
		dispatcher.execute(action, handler);
	}
	
	@Override
	public void onRangeChangeEvent(int start, int pageSize) {
		System.out.println("Calling range change event: start="+start+" size="+pageSize);
		GetEntityListAction action = new GetEntityListAction(EntityType.BUSINESS_ACCOUNT);
		action.setNeedTotalEntityCount(true);
		action.setPage(start);
		action.setPageSize(pageSize);
		dispatcher.execute(action, handler);
	};
	
	private class EntityListHandler extends DispatcherCallbackAsync<GetEntityResult> {
		@Override
		public void onSuccess(GetEntityResult result) {
			List<BusinessAccountDTO> accounts = new ArrayList<BusinessAccountDTO>();
			if (result.getEntityType() == EntityType.BUSINESS_ACCOUNT) {
				for(AccountDTO acct : result.getAccounts()) {
					accounts.add((BusinessAccountDTO)acct);
				}
			}
			
			if (result.getCount() != null) {
				view.setTotalRowCount(result.getCount());
			}
			view.display(accounts);
			panel.setWidget(view);
		}
		
		public void onFailure(Throwable caught) {
			// TODO
			Window.alert(caught.getLocalizedMessage());
		}
	}
	

	@Override
	public void bind() {
		view.setPresenter(this);
	}

	@Override
	public void fetchData() {
	}

	@Override
	public void go(AcceptsOneWidget container) {
	}
}
