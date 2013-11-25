package com.ziplly.app.client.activities;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.BusinessAccountSettingsPlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.places.PersonalAccountSettingsPlace;
import com.ziplly.app.client.view.NavView.NavPresenter;
import com.ziplly.app.client.view.View;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.view.event.LogoutEvent;
import com.ziplly.app.client.view.handler.LoginEventHandler;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.shared.LogoutAction;
import com.ziplly.app.shared.LogoutResult;

public class NavActivity extends AbstractActivity implements NavPresenter {
	private INavView view;

	public static interface INavView extends View<NavPresenter> {
		void showAccountLinks(boolean show);
		void onLogout();
	}
	
	@Inject
	public NavActivity(CachingDispatcherAsync dispatcher, EventBus eventBus,
			PlaceController placeController, ApplicationContext ctx, INavView view) {
		super(dispatcher, eventBus, placeController, ctx);
		this.view = view;

		setupHandlers();
	}

	private void setupHandlers() {
		eventBus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {
			@Override
			public void onEvent(LoginEvent event) {
				view.showAccountLinks(true);
			}
		});
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		bind();
		if (ctx.getAccount() != null) {
			view.showAccountLinks(true);
		}
		panel.setWidget(view);
	}

	@Override
	public void fetchData() {
	}

	@Override
	public void go(AcceptsOneWidget container) {
	}

	@Override
	public void bind() {
		view.setPresenter(this);
	}
	
	@Override
	public void logout() {
		DispatcherCallbackAsync<LogoutResult> dispatcherCallback = new DispatcherCallbackAsync<LogoutResult>() {
			@Override
			public void onSuccess(LogoutResult result) {
				ctx.setAccount(null);
//				eventBus.fireEvent(new LogoutEvent());
				view.showAccountLinks(false);
				placeController.goTo(new LoginPlace());
			}
			
			@Override 
			public void onFailure(Throwable t) {
				t.getMessage();
			}
		};
		dispatcher.execute(new LogoutAction(ctx.getAccount().getUid()),dispatcherCallback);
	}
	
	@Override
	public void redirectToSettingsPage() {
		AccountDTO account = ctx.getAccount();
		if (account == null) {
			placeController.goTo(new LoginPlace());
		}

		if (account instanceof PersonalAccountDTO) {
			placeController.goTo(new PersonalAccountSettingsPlace());
		}
		else if (account instanceof BusinessAccountDTO) {
			placeController.goTo(new BusinessAccountSettingsPlace());
		} 
		else {
			throw new IllegalArgumentException();
		}
	}
}
