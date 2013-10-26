package com.ziplly.app.client.view;

import com.github.gwtbootstrap.client.ui.TabPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.AccountPresenter;
import com.ziplly.app.client.widget.AccountDetailsWidget;
import com.ziplly.app.client.widget.AccountWidget;
import com.ziplly.app.client.widget.LogoutWidget;
import com.ziplly.app.model.PersonalAccountDTO;

public class AccountView extends Composite implements IAccountView<PersonalAccountDTO> {

	private static AccountViewUiBinder uiBinder = GWT
			.create(AccountViewUiBinder.class);

	interface AccountViewUiBinder extends UiBinder<Widget, AccountView> {
	}

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

	AccountPresenter<PersonalAccountDTO> presenter;


	// private ConversationWidget cw;

	public AccountView() {
		this.accountWidget = new AccountWidget();
		this.accountDetailsWidget = new AccountDetailsWidget();
		initWidget(uiBinder.createAndBindUi(this));
		logoutWidget.setVisible(false);
		accountViewTabs.setVisible(false);
	}

	
	@Override
	public void setPresenter(AccountPresenter<PersonalAccountDTO> presenter) {
		this.presenter = presenter;
		accountWidget.setPresenter(presenter);
		logoutWidget.setPresenter(presenter);
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
		accountViewTabs.setVisible(true);
	}

	@Override
	public void displayLogoutWidget() {
		logoutWidget.setVisible(true);
	}


	@Override
	public void displayPublicProfile(PersonalAccountDTO account) {
		accountWidget.displayPublicProfile(account);

		profileSection.clear();
		profileSection.add(accountWidget);

		// mark as visible
		accountViewTabs.setVisible(true);
	}

	public void clear() {
		accountWidget.clear();
		logoutWidget.setVisible(false);
		accountViewTabs.setVisible(false);
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
	public void setImageUploadUrl(String url) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void addUploadFormHandler(SubmitCompleteHandler submitCompleteHandler) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void displayProfileImagePreview(String imageUrl) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void resetUploadForm() {
		// TODO Auto-generated method stub
		
	}
}
