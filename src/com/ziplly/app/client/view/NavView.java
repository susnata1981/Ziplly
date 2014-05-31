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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.NavActivity.INavView;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.client.places.AboutPlace;
import com.ziplly.app.client.places.BusinessPlace;
import com.ziplly.app.client.places.ConversationPlace;
import com.ziplly.app.client.places.CouponExplorerPlace;
import com.ziplly.app.client.places.CouponReportPlace;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.places.ResidentPlace;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.client.view.factory.AbstractValueFormatterFactory;
import com.ziplly.app.client.view.factory.AbstractValueFormatterFactory.Formatter;
import com.ziplly.app.client.view.factory.AccountFormatter;
import com.ziplly.app.client.view.factory.BasicDataFormatter;
import com.ziplly.app.client.view.factory.ValueFamilyType;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.model.AccountNotificationDTO;
import com.ziplly.app.model.LocationDTO;

public class NavView extends Composite implements INavView {
	private static final String BLOG_LINK = "http://ziplly.blogspot.com";

	private static NavigationUiBinder uiBinder = GWT.create(NavigationUiBinder.class);

	public static interface NavPresenter extends Presenter {
		void logout();

		void redirectToSettingsPage();

		void onNotificationLinkClick(AccountNotificationDTO an);

		void switchLocation(LocationDTO location);

		void showTransactions();
	}

	interface Style extends CssResource {
		String notificationLink();
	}

	interface NavigationUiBinder extends UiBinder<Widget, NavView> {
	}

	@SuppressWarnings("unchecked")
	Formatter<AccountNotificationDTO> formatter =
	    (Formatter<AccountNotificationDTO>) AbstractValueFormatterFactory
	        .getValueFamilyFormatter(ValueFamilyType.ACCOUNT_NOTIFICATION);

	BasicDataFormatter basicDataFormatter = (BasicDataFormatter) AbstractValueFormatterFactory
	    .getValueFamilyFormatter(ValueFamilyType.BASIC_DATA_VALUE);

	AccountFormatter accountFormatter = (AccountFormatter) AbstractValueFormatterFactory
	    .getValueFamilyFormatter(ValueFamilyType.ACCOUNT_INFORMATION);

	@UiField
	Style style;

	@UiField
	Image logo;

	@UiField
	NavLink loginLink;

	@UiField
	Dropdown accountDropdown;
	@UiField
	NavLink accountLink;
	@UiField
	NavLink transactionLink;
	@UiField
	NavLink reportingLink;
	@UiField
	NavLink messageLink;

	@UiField
	NavLink settingsLink;
	@UiField
	NavLink logoutLink;

	@UiField
	NavLink homeLink;
	@UiField
	NavLink couponLink;
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

	@UiField
	Dropdown locationDropdown;

	NavPresenter presenter;
	List<AccountNotificationDTO> accountNotifications;

	public NavView() {
		initWidget(uiBinder.createAndBindUi(this));
		showAccountLinks(false);
		showAccountNotificationPanel(false);
		notifications.clear();

		logo.setUrl(ZResources.IMPL.zipllyLogo().getSafeUri().asString());
		logo.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.goTo(new HomePlace());
			}
		});
		
		locationDropdown.setVisible(false);
		reportingLink.setVisible(false);
		couponLink.setVisible(false);
	}

	private void showAccountNotificationPanel(boolean b) {
		Display display = b == true ? Display.BLOCK : Display.NONE;
		unreadNotificationCountPanel.getElement().getStyle().setDisplay(display);
	}

	@Override
	public void showAccountLinks(boolean value) {
		accountLink.setVisible(value);
		messageLink.setVisible(value);
		settingsLink.setVisible(value);
		notifications.setVisible(value);
		logoutLink.setVisible(value);
		accountDropdown.setVisible(value);
		loginLink.setVisible(!value);
	}

	@UiHandler("accountLink")
	void accountDetails(ClickEvent event) {
		presenter.goTo(new PersonalAccountPlace());
	}

	@UiHandler("transactionLink")
	void displayTransactions(ClickEvent event) {
		presenter.showTransactions();
	}
	
	@UiHandler("homeLink")
	void home(ClickEvent event) {
		presenter.goTo(new HomePlace());
	}

	 @UiHandler("couponLink")
	void coupon(ClickEvent event) {
	  presenter.goTo(new CouponExplorerPlace());
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

	@UiHandler("loginLink")
	void login(ClickEvent event) {
		presenter.goTo(new LoginPlace());
	}

	@UiHandler("reportingLink")
	void report(ClickEvent event) {
		presenter.goTo(new CouponReportPlace());
	}
	
	@Override
	public void clear() {
		showAccountLinks(false);
		displayLocationDropdown(false);
	}

	@Override
	public void setUnreadMessageCount(int count, boolean show) {
		if (count == 0) {
			if (show) {
				displayUnreadMessageCount(count);
			}
		} else if (count > 0) {
			displayUnreadMessageCount(count);
		}
	}

	private void displayUnreadMessageCount(int count) {
		messageLink.setText("Messages (" + count + ")");
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
		// String imageUrl = accountFormatter.format(an.getSender(),
		// ValueType.TINY_IMAGE_VALUE);
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
		HTMLPanel panel =
		    new HTMLPanel("<span>" + StringConstants.NO_NEW_NOTIFICATION_MESSAGE + "</span>");
		notifications.add(panel);
	}

	@UiHandler("blogLink")
	public void blog(ClickEvent event) {
		Window.open(BLOG_LINK, "_blank", "");
	}
	
	@UiHandler("aboutLink")
	public void displayAboutView(ClickEvent event) {
		presenter.goTo(new AboutPlace());
	}

	@Override
	public void displayLocationsDropdown(List<LocationDTO> locations) {
		locationDropdown.clear();
		if (locations != null && locations.size() > 0) {
			for (final LocationDTO location : locations) {
				NavLink locationLink = new NavLink(location.getNeighborhood().getName());
				locationLink.setText(location.getNeighborhood().getName());
				locationLink.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						// Switch
						presenter.switchLocation(location);
					}

				});
				locationDropdown.add(locationLink);
			}
			locationDropdown.setVisible(true);
		}
	}

	@Override
	public void displayLocationDropdown(boolean show) {
		locationDropdown.setVisible(show);
	}

	@Override
  public void displayReportingMenu() {
		reportingLink.setVisible(true);
  }
}
