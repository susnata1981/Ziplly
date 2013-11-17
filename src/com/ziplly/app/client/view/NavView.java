package com.ziplly.app.client.view;

import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.NavActivity.INavView;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.client.places.ConversationPlace;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.places.ResidentPlace;

public class NavView extends Composite implements INavView {
	private static NavigationUiBinder uiBinder = GWT
			.create(NavigationUiBinder.class);

	public static interface NavPresenter extends Presenter {
		void logout();
		void redirectToSettingsPage();
	}
	
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

	private NavPresenter presenter;

	public NavView() {
		initWidget(uiBinder.createAndBindUi(this));
		showAccountLinks(false);
	}

	@Override
	public void showAccountLinks(boolean value) {
		messageLink.setVisible(value);
		settingsLink.setVisible(value);
		logoutLink.setVisible(value);
	}

	@UiHandler("accountLink")
	void accountDetails(ClickEvent event) {
		presenter.goTo(new LoginPlace());
	}

	@UiHandler("homeLink")
	void home(ClickEvent event) {
		presenter.goTo(new HomePlace());
	}

	@UiHandler("residentsLink")
	void viewResidents(ClickEvent event) {
		presenter.goTo(new ResidentPlace());
	}

	@UiHandler("settingsLink")
	void viewSettings(ClickEvent event) {
		presenter.redirectToSettingsPage();
	}

	@UiHandler("logoutLink")
	void logout(ClickEvent event) {
		onLogout();
	}
	
	@UiHandler("messageLink")
	void getMessage(ClickEvent event) {
		presenter.goTo(new ConversationPlace());
	}

	@Override
	public void clear() {
	}

	@Override
	public void setPresenter(NavPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void onLogout() {
		presenter.logout();
	}
}
