package com.ziplly.app.client.view;

import com.github.gwtbootstrap.client.ui.TabPanel;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.AccountActivityPresenter;
import com.ziplly.app.client.widget.AccountDetailsWidget;
import com.ziplly.app.client.widget.AccountWidget;
import com.ziplly.app.client.widget.LoginWidget;
import com.ziplly.app.client.widget.LogoutWidget;
import com.ziplly.app.model.AccountDTO;

public class AccountView extends Composite implements IAccountView, LoginAwareView {

	private static AccountViewUiBinder uiBinder = GWT
			.create(AccountViewUiBinder.class);

	interface AccountViewUiBinder extends UiBinder<Widget, AccountView> {
	}

	@UiField
	HTMLPanel loginMessagePanel;

	@UiField
	LoginWidget loginWidget;

	@UiField
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

	private AccountActivityPresenter presenter;

	// private ConversationWidget cw;

	public AccountView() {
		this.accountWidget = new AccountWidget();
		this.accountDetailsWidget = new AccountDetailsWidget(presenter);
		initWidget(uiBinder.createAndBindUi(this));
		
		loginWidget.setVisible(false);		
		logoutWidget.setVisible(false);
		accountViewTabs.setVisible(false);
	}

	public void displayLoginWidget() {
		loginWidget.setVisible(true);
		logoutWidget.setVisible(false);
	}

	public void displayLogoutWidget() {
		logoutWidget.setVisible(true);
		loginWidget.setVisible(false);
	}
	
	@Override
	public void setPresenter(AccountActivityPresenter presenter) {
		this.presenter = presenter;
		accountWidget.setPresenter(presenter);
		loginWidget.setPresenter(presenter);
		logoutWidget.setPresenter(presenter);
	}

	@Override
	public void display(AccountDTO account) {
		if (account == null) {
			throw new IllegalArgumentException();
		}
		accountWidget.displayAccount(account);

		profileSection.clear();
		profileSection.add(accountWidget);

		// mark as visible
		accountViewTabs.setVisible(true);
	}

	@Override
	public void displayLoginErrorMessage(String msg, AlertType type) {
		loginWidget.setMessage(msg, type);
	}
	
	@Override
	public void resetLoginForm() {
		loginWidget.resetForm();
	}
	
	@Override
	public void onSave() {
		// TODO Auto-generated method stub
	}

	public void clear() {
		accountWidget.clear();
		loginWidget.setVisible(false);		
		logoutWidget.setVisible(false);
		accountViewTabs.setVisible(false);
	}

	public void displayAccountUpdateSuccessfullMessage() {
		accountWidget.getEditAccountDetailsWidget().displaySuccessMessage();
	}
	
	public void displayAccountUpdateFailedMessage() {
		accountWidget.getEditAccountDetailsWidget().displayErrorMessage();
	}

	public void displayPublicProfile(AccountDTO account) {
		accountWidget.displayPublicProfile(account);

		profileSection.clear();
		profileSection.add(accountWidget);

		// mark as visible
		accountViewTabs.setVisible(true);
	}

	public void clearTweet() {
		accountWidget.clearTweet();
	}
}
