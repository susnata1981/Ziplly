package com.ziplly.app.client.view;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.view.handler.LoginEventHandler;
import com.ziplly.app.model.NavLinkEnum;

public class NavView extends Composite {
	private static NavigationUiBinder uiBinder = GWT.create(NavigationUiBinder.class);

	interface NavigationUiBinder extends UiBinder<Widget, NavView> {
	}

	@UiField
	NavLink accountsLink;
	
	@UiField
	NavLink homeLink;
	
	private List<NavLink> navLinks = new ArrayList<NavLink>();
	
	private SimpleEventBus eventBus;

	public NavView(SimpleEventBus eventBus) {
		this.eventBus = eventBus;
		initWidget(uiBinder.createAndBindUi(this));
		setup();
	}

	private void setup() {
		navLinks.add(homeLink);
		navLinks.add(accountsLink);
		setupEventHandlers();
	}

	private void setupEventHandlers() {
		eventBus.addHandler(LoginEvent.TYPE, new LoginHandler(this));
	}

	@UiHandler("accountsLink")
	void accountDetails(ClickEvent event) {
		setActive(accountsLink);
		History.newItem("account");
	}
	
	@UiHandler("homeLink")
	void home(ClickEvent event) {
		setActive(homeLink);
		History.newItem("home");
	}
	
	private void setActive(NavLink link) {
		for(NavLink nl: navLinks) {
			nl.setActive(false);
		}
		link.setActive(true);
	}
	
	public void setActive(NavLinkEnum value) {
		switch(value) {
		case ACCOUNT:
			setActive(accountsLink);
			break;
		case HOME:
		default:
			setActive(homeLink);
		}
	}
	
	private static class LoginHandler implements LoginEventHandler {
		private NavView navView;
		public LoginHandler(NavView navView) {
			this.navView = navView;
		}

		@Override
		public void onEvent(LoginEvent event) {
			navView.setActive(navView.accountsLink);
		}
	}

}
