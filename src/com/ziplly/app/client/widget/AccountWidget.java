package com.ziplly.app.client.widget;

import java.util.HashMap;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.AccountActivityPresenter;
import com.ziplly.app.client.view.View;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountSettingDTO;
import com.ziplly.app.model.InterestDTO;

public class AccountWidget extends Composite implements
		View<AccountActivityPresenter> {

	private static AccountWidgetUiBinder uiBinder = GWT
			.create(AccountWidgetUiBinder.class);

	interface AccountWidgetUiBinder extends UiBinder<Widget, AccountWidget> {
	}

	@UiField
	Image profileImageUrl;

	@UiField
	Element name;

	@UiField
	Element introduction;

	@UiField
	Anchor editBasicInfoLink;

	@UiField
	Anchor editInterestLink;

	@UiField
	Anchor editLocationLink;

	@UiField
	Anchor editOccupationLink;

	@UiField
	Anchor viewPublicProfileLink;

	@UiField
	HTMLPanel interestListPanel;

	@UiField
	Element lastLogin;

	@UiField
	Element cityLabel;

	@UiField
	Element stateLabel;

	@UiField
	Element occupation;

	@UiField
	Button sendMsgBtn;

	@UiField
	TextArea tweetTextBox;
	@UiField
	HelpInline tweetHelpInline;
	@UiField
	Button tweetBtn;

	@UiField
	HTMLPanel accountWidgetRoot;

	@UiField
	Element numberOfLikes;

	@UiField
	Element numberOfComments;

	@UiField
	Element numberOfPosts;

	@UiField
	Form tweetForm;
	
	private EditAccountDetailsWidget eadw;
	boolean displayEdit;
	AccountActivityPresenter presenter;
	private AccountDTO account;
	Map<AccountDetailsType, Anchor> editLinksMap = new HashMap<AccountDetailsType, Anchor>();

	@UiFactory
	public MyBundle createTheBundle() {
		MyBundle.INSTANCE.style().ensureInjected();
		return MyBundle.INSTANCE;
	}

	public AccountWidget() {
		this.eadw = new EditAccountDetailsWidget();
		initWidget(uiBinder.createAndBindUi(this));

		editLinksMap.put(AccountDetailsType.BASICINFO, editBasicInfoLink);
		editLinksMap.put(AccountDetailsType.LOCATION, editLocationLink);
		editLinksMap.put(AccountDetailsType.OCCUPATION, editOccupationLink);
		editLinksMap.put(AccountDetailsType.INTEREST, editInterestLink);
		tweetForm.setVisible(false);
	}

	/*
	 * Displays personal account for logged in user
	 */
	public void displayAccount(AccountDTO account) {
		if (account == null) {
			throw new IllegalArgumentException();
		}

		this.account = account;
		for (AccountSettingDTO asd : account.getAccountSettings()) {
			populateSection(asd.getSection(), account);
			editLinksMap.get(asd.getSection()).setVisible(true);
		}
		tweetForm.setVisible(true);
	}

	/*
	 * Displays public profile of an user
	 */
	public void displayPublicProfile(AccountDTO account) {
		if (account == null) {
			return;
		}
		this.account = account;
		for (AccountSettingDTO asd : account.getAccountSettings()) {
			if (asd.getSection().equals(AccountDetailsType.BASICINFO)
					|| !asd.getSetting().equals(ShareSetting.PRIVATE)) {
				populateSection(asd.getSection(), account);
				editLinksMap.get(asd.getSection()).setVisible(false);
			}
		}
		tweetForm.setVisible(false);
	}

	protected void populateSection(AccountDetailsType section,
			AccountDTO account) {
		switch (section) {
		case BASICINFO:
			populateBasicInfo(account);
			break;
		case OCCUPATION:
			populateOccupation(account);
			break;
		case INTEREST:
			populateInterests(account);
			break;
		case LOCATION:
			populateLocation(account);
			break;
		default:
		}
	}

	protected void clearSection(AccountDetailsType section) {
		switch (section) {
		case BASICINFO:
			clearBasicInfo();
			break;
		case OCCUPATION:
			clearOccupation();
			break;
		case INTEREST:
			clearInterests();
			break;
		case LOCATION:
			clearLocation();
			break;
		default:
		}
	}

	private void clearLocation() {
		cityLabel.setInnerText("");
		stateLabel.setInnerText("");
	}

	private void clearInterests() {
		interestListPanel.clear();
	}

	private void clearOccupation() {

	}

	private void clearBasicInfo() {
		name.setInnerText("");
		introduction.setInnerText("");
		profileImageUrl.setUrl("");
		lastLogin.setInnerText("");
	}

	protected void populateLocation(AccountDTO account) {
		cityLabel.setInnerText(capitalize(account.getCity()));
		stateLabel.setInnerText(capitalize(account.getState()));
	}

	protected void populateOccupation(AccountDTO account) {
		occupation.setInnerText(account.getOccupation());
	}

	protected void populateBasicInfo(AccountDTO account) {
		name.setInnerText(account.getDisplayName());
		introduction.setInnerText(account.getIntroduction());
		if (account.getImageUrl() != null) {
			profileImageUrl.setUrl(account.getImageUrl());
		}
		String date = DateTimeFormat.getFormat("MM-dd-yyyy").format(
				account.getLastLoginTime());
		lastLogin.setInnerText(date);
	}

	protected void populateInterests(AccountDTO account) {
		interestListPanel.clear();
		for (InterestDTO interest : account.getInterests()) {
			interestListPanel.add(new Label(interest.getName()));
		}
	}

	@UiHandler("editBasicInfoLink")
	void editCategory(ClickEvent event) {
		showAccountInfoFormWidget(AccountDetailsType.BASICINFO);
	}

	@UiHandler("editInterestLink")
	void editInterest(ClickEvent event) {
		showAccountInfoFormWidget(AccountDetailsType.INTEREST);
	}

	@UiHandler("editLocationLink")
	void editLocation(ClickEvent event) {
		showAccountInfoFormWidget();
	}

	@UiHandler("sendMsgBtn")
	void sendMessage(ClickEvent event) {

	}

	@UiHandler("tweetBtn")
	void tweet(ClickEvent event) {
		// validate()
		if (tweetTextBox.getText() != null) {
			String content = tweetTextBox.getText().trim();
			presenter.tweet(content);
		}
	}

	@UiHandler("viewPublicProfileLink")
	void showPublicProfile(ClickEvent event) {
		presenter.displayPublicProfile(account.getAccountId());
	}

	void showAccountInfoFormWidget() {
		eadw.show(AccountDetailsType.BASICINFO);
	}

	void showAccountInfoFormWidget(AccountDetailsType tab) {
		eadw.displayAccount(account);
		eadw.show(tab);
	}

	public void clear() {
	}

	String capitalize(String str) {
		if (str == null || str.length() == 0) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		sb.append(Character.toUpperCase(str.charAt(0)))
				.append(str.substring(1));
		return sb.toString();
	}

	@Override
	public void setPresenter(AccountActivityPresenter presenter) {
		this.presenter = presenter;
		eadw.setPresenter(presenter);
	}

	public EditAccountDetailsWidget getEditAccountDetailsWidget() {
		return eadw;
	}

	public void clearTweet() {
		tweetTextBox.setText("");
	}
}
