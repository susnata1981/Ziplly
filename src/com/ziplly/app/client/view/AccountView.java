package com.ziplly.app.client.view;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.widget.AccountWidget;
import com.ziplly.app.client.widget.CommunityWallWidget;
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
	HTMLPanel loginMessageLabel;

	@UiField
	Button fbLoginButtonOnMainPage;

	@UiField(provided = true)
	LoginWidget loginWidget;

	@UiField(provided = true)
	LogoutWidget logoutWidget;

	@UiField
	HTMLPanel profileSection;

	@UiField
	TabPanel accountViewTabs;

	// @UiField
	// HTMLPanel profileStatSection;

	@UiField
	HTMLPanel conversationSection;

	@UiField
	HTMLPanel settingsPanel;
	
	private AccountWidget accountWidget;
	private CommunityWallWidget cww;
	private ConversationWidget cw;

	public AccountView(SimpleEventBus eventBus) {
		super(eventBus);
	}

	@Override
	protected void initWidget() {
		// cw = WidgetFactory.getConversationWidget(getService(), eventBus);
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	protected void postInitWidget() {
		if (userLoggedIn()) {
			displayLoginRequiredMessage(false);
		} else {
			displayLoginRequiredMessage(true);
			accountViewTabs.setVisible(false);
		}
	}

	boolean userLoggedIn() {
		return getAccount() != null;
	}

	@Override
	protected void setupUiElements() {
		accountWidget = WidgetFactory.getAccountWidget(eventBus);
		cww = new CommunityWallWidget(eventBus);
		this.loginWidget = WidgetFactory.getLoginWidget(getService(), eventBus);
		this.logoutWidget = WidgetFactory.getLogoutWidget(getService(),
				eventBus);
	}

	@Override
	protected void internalOnUserLogin() {
		// AccountView.this.ad = ad;
		if (userLoggedIn()) {
			displayLoginRequiredMessage(false);
			refresh();
		}
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

	@UiHandler("fbLoginButtonOnMainPage")
	void fbLogin(ClickEvent event) {
		try {
			Window.Location.replace(authConfig.getAuthorizationUrl());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	void displayLoginRequiredMessage(boolean shouldDisplay) {
		if (shouldDisplay) {
			loginWidget.show();
		}
		loginMessageLabel.setVisible(shouldDisplay);
		logoutWidget.setVisible(!shouldDisplay);
	}
}
