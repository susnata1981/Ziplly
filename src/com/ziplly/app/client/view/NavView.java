package com.ziplly.app.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.ziplly.app.client.places.AccountPlace;
import com.ziplly.app.client.places.HomePlace;

public class NavView extends Composite {
	private static NavigationUiBinder uiBinder = GWT.create(NavigationUiBinder.class);

	interface NavigationUiBinder extends UiBinder<Widget, NavView> {
	}

	@UiField
	Anchor accountsLink;
	@UiField
	Anchor homeLink;
	@UiField
	Anchor aboutLink;
	
	EventBus eventBus;
	PlaceController placeController;
	
	@Inject
	public NavView(EventBus eventBus,PlaceController placeController) {
		this.eventBus = eventBus;
		this.placeController = placeController;
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("accountsLink")
	void accountDetails(ClickEvent event) {
		placeController.goTo(new AccountPlace());
	}
	
	@UiHandler("homeLink")
	void home(ClickEvent event) {
		placeController.goTo(new HomePlace());
	}
}
