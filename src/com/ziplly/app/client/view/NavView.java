package com.ziplly.app.client.view;

import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.BusinessAccountSettingsPlace;
import com.ziplly.app.client.places.ConversationPlace;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.places.PersonalAccountSettingsPlace;
import com.ziplly.app.client.places.ResidentPlace;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.view.event.LogoutEvent;
import com.ziplly.app.client.view.handler.LoginEventHandler;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.shared.LogoutAction;
import com.ziplly.app.shared.LogoutResult;

public class NavView extends Composite {
	private static NavigationUiBinder uiBinder = GWT
			.create(NavigationUiBinder.class);

	interface NavigationUiBinder extends UiBinder<Widget, NavView> {
	}

	@UiField
	NavLink accountLink;
	@UiField
	NavLink messageLink;
	@UiField
	NavLink settingsLink;
	@UiField
	NavLink logoutLink;

	@UiField
	NavLink homeLink;
	@UiField
	NavLink residentsLink;
	@UiField
	NavLink aboutLink;

	EventBus eventBus;
	PlaceController placeController;
	private ApplicationContext ctx;
	private CachingDispatcherAsync dispatcher;

	@Inject
	public NavView(ApplicationContext ctx, CachingDispatcherAsync dispatcher,
			EventBus eventBus, PlaceController placeController) {
		this.ctx = ctx;
		this.dispatcher = dispatcher;
		this.eventBus = eventBus;
		this.placeController = placeController;
		initWidget(uiBinder.createAndBindUi(this));
		setAccountLinks(false);

		if (ctx.getAccount() != null) {
			setAccountLinks(true);
		}

		eventBus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {
			@Override
			public void onEvent(LoginEvent event) {
				setAccountLinks(true);
			}
		});
	}

	private void setAccountLinks(Boolean value) {
		messageLink.setVisible(value);
		residentsLink.setVisible(value);
		settingsLink.setVisible(value);
		logoutLink.setVisible(value);
	}

	@UiHandler("accountLink")
	void accountDetails(ClickEvent event) {
		placeController.goTo(new LoginPlace());
	}

	@UiHandler("homeLink")
	void home(ClickEvent event) {
		placeController.goTo(new HomePlace());
	}

	@UiHandler("residentsLink")
	void viewResidents(ClickEvent event) {
		placeController.goTo(new ResidentPlace());
	}

	@UiHandler("settingsLink")
	void viewSettings(ClickEvent event) {
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

	@UiHandler("logoutLink")
	void logout(ClickEvent event) {
		DispatcherCallbackAsync<LogoutResult> dispatcherCallback = new DispatcherCallbackAsync<LogoutResult>() {
			@Override
			public void onSuccess(LogoutResult result) {
				ctx.setAccount(null);
				eventBus.fireEvent(new LogoutEvent());
				placeController.goTo(new LoginPlace());
				setAccountLinks(false);
			}
			
			@Override 
			public void onFailure(Throwable t) {
				t.getMessage();
			}
		};
		dispatcher.execute(new LogoutAction(ctx.getAccount().getUid()),dispatcherCallback);
	}
	
	@UiHandler("messageLink")
	void getMessage(ClickEvent event) {
		placeController.goTo(new ConversationPlace());
	}
}
