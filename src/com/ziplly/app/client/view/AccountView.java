package com.ziplly.app.client.view;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.TabPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.widget.AccountDetailsWidget;
import com.ziplly.app.client.widget.AccountWidget;
import com.ziplly.app.client.widget.ConversationWidget;
import com.ziplly.app.client.widget.LoginWidget;
import com.ziplly.app.client.widget.LogoutWidget;
import com.ziplly.app.model.Category;

public class AccountView extends AbstractAccountView {

	private static AccountViewUiBinder uiBinder = GWT
			.create(AccountViewUiBinder.class);

	interface AccountViewUiBinder extends UiBinder<Widget, AccountView> {
	}

	List<Category> selectedCategories = new ArrayList<Category>();

	@UiField
	HTMLPanel loginMessagePanel;

	@UiField(provided = true)
	LoginWidget loginWidget;

	@UiField(provided = true)
	LogoutWidget logoutWidget;

	@UiField
	HTMLPanel profileSection;

	@UiField
	TabPanel accountViewTabs;

	@UiField
	HTMLPanel conversationSection;

	@UiField
	HTMLPanel settingsPanel;

	@UiField(provided = true)
	AccountDetailsWidget accountDetailsWidget;

	private AccountWidget accountWidget;
	private ConversationWidget cw;
	
	@Inject
	public AccountView(CachingDispatcherAsync dispatcher, SimpleEventBus eventBus) {
		super(dispatcher, eventBus);
	}

	@Override
	protected void initWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	protected void postInitWidget() {
		if (isUserLoggedIn()) {
			displayLoginRequiredMessage(false);
			logoutWidget.setVisible(true);
		} else {
			displayLoginRequiredMessage(true);
			logoutWidget.setVisible(false);
			accountViewTabs.setVisible(false);
		}
	}

	@Override
	protected void setupUiElements() {
		this.accountWidget = new AccountWidget(dispatcher, eventBus, false);
		this.loginWidget = new LoginWidget(dispatcher, eventBus);
		this.logoutWidget = new LogoutWidget(dispatcher, eventBus);
//		this.logoutWidget = WidgetFactory.getLogoutWidget(dispatcher, eventBus);
		this.accountDetailsWidget = new AccountDetailsWidget(dispatcher, eventBus);
	}

	@Override
	protected void internalOnUserLogin() {
		System.out.println("Calling internalOnUserLogin inside AccountView");
		displayLoginRequiredMessage(false);
		loginMessagePanel.setVisible(false);
		refresh();
	}

	void refresh() {
		displayProfile();
	}

	void displayProfile() {
		accountWidget.displayAccount(getAccount());

		profileSection.clear();
		profileSection.add(accountWidget);

		// update categories
		selectedCategories.clear();

		// mark as visible
		accountViewTabs.setVisible(true);
	}

	void displayLoginRequiredMessage(boolean shouldDisplay) {
		if (shouldDisplay) {
			loginWidget.setVisible(shouldDisplay);
		} else {
			logoutWidget.setVisible(!shouldDisplay);
		}
	}
}
