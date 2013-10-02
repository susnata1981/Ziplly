package com.ziplly.app.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

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
	
	private SimpleEventBus eventBus;

	public NavView(SimpleEventBus eventBus) {
		this.eventBus = eventBus;
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("accountsLink")
	void accountDetails(ClickEvent event) {
		History.newItem("account");
	}
	
	@UiHandler("homeLink")
	void home(ClickEvent event) {
		History.newItem("main");
	}
}
