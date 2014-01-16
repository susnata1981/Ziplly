package com.ziplly.app.client.view;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.NavActivity.INavView;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.client.places.AboutPlace;
import com.ziplly.app.client.places.BusinessPlace;
import com.ziplly.app.client.places.ConversationPlace;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.places.ResidentPlace;
import com.ziplly.app.client.view.factory.AbstractValueFormatterFactory;
import com.ziplly.app.client.view.factory.AbstractValueFormatterFactory.Formatter;
import com.ziplly.app.client.view.factory.ValueFamilyType;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.model.AccountNotificationDTO;

public class NavView extends Composite implements INavView {
	private static NavigationUiBinder uiBinder = GWT.create(NavigationUiBinder.class);

	public static interface NavPresenter extends Presenter {
		void logout();

		void redirectToSettingsPage();

		void onNotificationLinkClick(AccountNotificationDTO an);
	}

	interface Style extends CssResource {
		String notificationLink();
	}

	interface NavigationUiBinder extends UiBinder<Widget, NavView> {
	}

	@SuppressWarnings("unchecked")
	Formatter<AccountNotificationDTO> formatter = (Formatter<AccountNotificationDTO>) AbstractValueFormatterFactory
			.getValueFamilyFormatter(ValueFamilyType.ACCOUNT_NOTIFICATION);

	@UiField
	Style style;

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
	NavLink businessListLink;
	
	@UiField
	NavLink aboutLink;

	@UiField
	Dropdown notifications;

	@UiField
	SpanElement unreadNotificationCountSpan;
	@UiField
	HTMLPanel unreadNotificationCountPanel;
	
	NavPresenter presenter;
	List<AccountNotificationDTO> accountNotifications;
	
	public NavView() {
		initWidget(uiBinder.createAndBindUi(this));
		showAccountLinks(false);
		showAccountNotificationPanel(false);
		notifications.clear();
	}

	private void showAccountNotificationPanel(boolean b) {
		Display display = b == true ? Display.BLOCK : Display.NONE;
		unreadNotificationCountPanel.getElement().getStyle().setDisplay(display);
	}

	@Override
	public void showAccountLinks(boolean value) {
		messageLink.setVisible(value);
		settingsLink.setVisible(value);
		notifications.setVisible(value);
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

	@UiHandler("businessListLink")
	void viewBusinessList(ClickEvent event) {
		presenter.goTo(new BusinessPlace());
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

	public NavPresenter getPresenter() {
		return presenter;
	}

	@Override
	public void onLogout() {
		presenter.logout();
	}

	public void clearNotifications() {
		notifications.clear();
	}

	@Override
	public void addNotification(final AccountNotificationDTO an) {
		Anchor anchor = new Anchor();
		anchor.setStyleName(style.notificationLink());
		notifications.add(anchor);
		ValueType valueType = ValueType.valueOf(an.getType().name());
		anchor.getElement().setInnerHTML(formatter.format(an, valueType));
		ClickHandler handler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.onNotificationLinkClick(an);
			}
		};
		anchor.addClickHandler(handler);
	}

	@Override
	public void displayAccountNotifications(List<AccountNotificationDTO> accountNotifications) {
		notifications.clear();
		this.accountNotifications = accountNotifications;
		int count = accountNotifications.size();
		updateNotificationCount(count);
		if (count > 0) {
			for (final AccountNotificationDTO an : accountNotifications) {
				addNotification(an);
			}
		} else {
			displayNoNewNotification();
		}
	}

	@Override
	public void updateNotificationCount(int count) {
		if (count > 0) {
			showAccountNotificationPanel(true);
			unreadNotificationCountSpan.setInnerText(Integer.toString(count));
		} else {
			showAccountNotificationPanel(false);
		}
	}

	@Override
	public void displayNoNewNotification() {
		HTMLPanel panel = new HTMLPanel("<span>"+StringConstants.NO_NEW_NOTIFICATION_MESSAGE+"</span>");
		notifications.add(panel);
	}
	
	@UiHandler("aboutLink")
	public void displayAboutView(ClickEvent event) {
		presenter.goTo(new AboutPlace());
	}
}
