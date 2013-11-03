package com.ziplly.app.client.view;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.AccountPresenter;
import com.ziplly.app.client.widget.AccountWidget;
import com.ziplly.app.client.widget.ConversationWidget;
import com.ziplly.app.client.widget.LogoutWidget;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.PersonalAccountDTO;

public class AccountView extends Composite implements IAccountView<PersonalAccountDTO> {

	private static AccountViewUiBinder uiBinder = GWT
			.create(AccountViewUiBinder.class);

	interface AccountViewUiBinder extends UiBinder<Widget, AccountView> {
	}

//	@UiField
//	LogoutWidget logoutWidget;

	@UiField
	HTMLPanel profileSection;

//	@UiField
//	TabPanel accountViewTabs;
//
//	@UiField
//	Tab messagesTab;
//	
//	@UiField
//	HTMLPanel mainContent;
//	
//	@UiField
//	HTMLPanel settingsPanel;

//	@UiField
//	ConversationWidget conversationWidget;
	
	private AccountWidget accountWidget;

	AccountPresenter<PersonalAccountDTO> presenter;


	public AccountView() {
		this.accountWidget = new AccountWidget();
		initWidget(uiBinder.createAndBindUi(this));
//		logoutWidget.setVisible(false);
//		accountViewTabs.setVisible(false);
	}

	
	@Override
	public void setPresenter(AccountPresenter<PersonalAccountDTO> presenter) {
		this.presenter = presenter;
		accountWidget.setPresenter(presenter);
//		conversationWidget.setPresenter(presenter);
//		logoutWidget.setPresenter(presenter);
	}

	@Override
	public void displayProfile(PersonalAccountDTO account) {
		if (account == null) {
			throw new IllegalArgumentException();
		}
		displayLogoutWidget();
		accountWidget.displayAccount(account);
		profileSection.clear();
		profileSection.add(accountWidget);

		// mark as visible
//		accountViewTabs.setVisible(true);
	}

	@Override
	public void displayLogoutWidget() {
//		logoutWidget.setVisible(true);
	}


	@Override
	public void displayPublicProfile(PersonalAccountDTO account) {
		profileSection.clear();
		accountWidget.displayPublicProfile(account);
		profileSection.clear();
		profileSection.add(accountWidget);

		// mark as visible
//		accountViewTabs.setVisible(true);
//		accountViewTabs.selectTab(0);
	}

	@Override
	public void clear() {
		accountWidget.clear();
//		logoutWidget.setVisible(false);
//		accountViewTabs.setVisible(false);
//		conversationWidget.clear();
	}

	public void displayAccountUpdateSuccessfullMessage() {
		accountWidget.getEditAccountDetailsWidget().displaySuccessMessage();
	}
	
	public void displayAccountUpdateFailedMessage() {
		accountWidget.getEditAccountDetailsWidget().displayErrorMessage();
	}

	public void clearTweet() {
		accountWidget.clearTweet();
	}

	@Override
	public void closeSendMessageWidget() {
		accountWidget.closeSendMessageModal();
	}


	@Override
	public void displayConversations(List<ConversationDTO> conversations) {
//		conversationWidget.displayConversations(conversations);
	}

}
