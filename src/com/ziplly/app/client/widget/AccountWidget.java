package com.ziplly.app.client.widget;

import java.util.HashMap;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Label;
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
import com.ziplly.app.client.activities.AccountPresenter;
import com.ziplly.app.client.view.View;
import com.ziplly.app.model.AccountSettingDTO;
import com.ziplly.app.model.InterestDTO;
import com.ziplly.app.model.PersonalAccountDTO;

public class AccountWidget extends Composite implements
		View<AccountPresenter<PersonalAccountDTO>> {

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
	HTMLPanel accountWidgetRoot;

	@UiField
	Element numberOfLikes;

	@UiField
	Element numberOfComments;

	@UiField
	Element numberOfPosts;

	@UiField
	TweetBox tweetBox;
	
	private EditAccountDetailsWidget eadw;
	boolean displayEdit;
	private PersonalAccountDTO account;
	Map<AccountDetailsType, Anchor> editLinksMap = new HashMap<AccountDetailsType, Anchor>();

	private AccountPresenter<PersonalAccountDTO> presenter;

	@UiFactory
	public MyBundle createTheBundle() {
		MyBundle.INSTANCE.style().ensureInjected();
		return MyBundle.INSTANCE;
	}

	public AccountWidget() {
		this.eadw = new EditAccountDetailsWidget();
		initWidget(uiBinder.createAndBindUi(this));

		editLinksMap.put(AccountDetailsType.EMAIL, editOccupationLink);
		editLinksMap.put(AccountDetailsType.OCCUPATION, editOccupationLink);
	}

	/*
	 * Displays personal account for logged in user
	 */
	public void displayAccount(PersonalAccountDTO account) {
		if (account == null) {
			throw new IllegalArgumentException();
		}

		this.account = account;
		for (AccountDetailsType adt : AccountDetailsType.values()) {
			populateSection(adt, account);
			editLinksMap.get(adt).setVisible(true);
		}
		sendMsgBtn.setVisible(false);
	}

	/*
	 * Displays public profile of an user
	 */
	public void displayPublicProfile(PersonalAccountDTO account) {
		if (account == null) {
			return;
		}
		this.account = account;

		if (account.getAccountSettings().size() == 0) {
			for (AccountDetailsType adt : AccountDetailsType.values()) {
				populateSection(adt, account);
				editLinksMap.get(adt).setVisible(false);
			}
		}

		for (AccountSettingDTO asd : account.getAccountSettings()) {
			if (asd.getSection().equals(AccountDetailsType.EMAIL)
					|| !asd.getSetting().equals(ShareSetting.PRIVATE)) {
				populateSection(asd.getSection(), account);
				editLinksMap.get(asd.getSection()).setVisible(false);
			}
		}
		sendMsgBtn.setVisible(true);
	}

	protected void populateSection(AccountDetailsType section,
			PersonalAccountDTO account) {
		switch (section) {
		case EMAIL:
			populateBasicInfo(account);
			break;
		case OCCUPATION:
			populateOccupation(account);
			break;
//		case INTEREST:
//			populateInterests(account);
//			break;
//		case LOCATION:
//			populateLocation(account);
//			break;
		default:
		}
	}

	protected void clearSection(AccountDetailsType section) {
		switch (section) {
		case EMAIL:
			clearBasicInfo();
			break;
		case OCCUPATION:
			clearOccupation();
			break;
//		case INTEREST:
//			clearInterests();
//			break;
//		case LOCATION:
//			clearLocation();
//			break;
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

	protected void populateLocation(PersonalAccountDTO account) {
	}

	protected void populateOccupation(PersonalAccountDTO account) {
		occupation.setInnerText(account.getOccupation());
	}

	protected void populateBasicInfo(PersonalAccountDTO account) {
		name.setInnerText(account.getDisplayName());
		introduction.setInnerText(account.getIntroduction());
		if (account.getImageUrl() != null) {
			profileImageUrl.setUrl(account.getImageUrl());
		}
		String date = DateTimeFormat.getFormat("MM-dd-yyyy").format(
				account.getLastLoginTime());
		lastLogin.setInnerText(date);
	}

	protected void populateInterests(PersonalAccountDTO account) {
		interestListPanel.clear();
		for (InterestDTO interest : account.getInterests()) {
			interestListPanel.add(new Label(interest.getName()));
		}
	}

//	@UiHandler("editBasicInfoLink")
//	void editCategory(ClickEvent event) {
//		showAccountInfoFormWidget(AccountDetailsType.BASICINFO);
//	}
//
//	@UiHandler("editInterestLink")
//	void editInterest(ClickEvent event) {
//		showAccountInfoFormWidget(AccountDetailsType.INTEREST);
//	}

	@UiHandler("editLocationLink")
	void editLocation(ClickEvent event) {
		showAccountInfoFormWidget();
	}

	SendMessageWidget smw;
	
	@UiHandler("sendMsgBtn")
	void sendMessage(ClickEvent event) {
		smw = new SendMessageWidget(account);
		smw.setPresenter(presenter);
		smw.show();
	}

	void showAccountInfoFormWidget() {
		eadw.show(AccountDetailsType.EMAIL);
	}

	void showAccountInfoFormWidget(AccountDetailsType tab) {
		eadw.displayAccount(account);
		eadw.show(tab);
	}

	public void clear() {
	}

	public EditAccountDetailsWidget getEditAccountDetailsWidget() {
		return eadw;
	}

	public void clearTweet() {
		tweetBox.clear();
	}

	@Override
	public void setPresenter(AccountPresenter<PersonalAccountDTO> presenter) {
		this.presenter = presenter;
		eadw.setPresenter(presenter);
		tweetBox.setPresenter(presenter);
	}

	public void closeSendMessageModal() {
		smw.hide();
	}
}