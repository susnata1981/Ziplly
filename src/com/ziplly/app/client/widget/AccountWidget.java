package com.ziplly.app.client.widget;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Image;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.view.AbstractAccountView;
import com.ziplly.app.client.view.event.AccountUpdateEvent;
import com.ziplly.app.client.view.handler.AccountUpdateEventHandler;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountDetails;

public class AccountWidget extends AbstractAccountView {

	private static AccountWidgetUiBinder uiBinder = GWT
			.create(AccountWidgetUiBinder.class);
	private Logger logger = Logger.getLogger("AccountWidget");

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
	Anchor viewPublicProfile;
	
	@UiField
	HTMLPanel interestList;

	@UiField
	Element lastLogin;

	@UiField
	Element cityLabel;

	@UiField
	Element stateLabel;

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

	private EditAccountDetailsWidget eadw;
	private boolean displayEdit;

	@UiFactory
	public MyBundle createTheBundle() {
		MyBundle.INSTANCE.style().ensureInjected();
		return MyBundle.INSTANCE;
	}

	public AccountWidget(SimpleEventBus eventBus, boolean displayEdit) {
		super(eventBus);
		this.displayEdit = displayEdit;
		logger.log(Level.INFO, "AccountWidget created");
	}

	@Override
	protected void initWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	protected void postInitWidget() {
	}

	@Override
	protected void setupUiElements() {
		eadw = new EditAccountDetailsWidget(eventBus);
	}

	@Override
	protected void setupHandlers() {
		eventBus.addHandler(AccountUpdateEvent.TYPE,
				new AccountUpdateEventHandler() {
					@Override
					public void onEvent(AccountUpdateEvent event) {
						// TODO
					}
				});
	}

	public void displayAccount(AccountDTO account) {
		this.account = account;
		populateProfile();
		// TODO
		// if (!isAccountComplete()) {
		// showAccountInfoFormWidget();
		// }
	}

	void populateProfile() {
		name.setInnerText(account.getDisplayName());
		introduction.setInnerText(account.getIntroduction());
		
		if (account.getImageUrl() != null) {
			profileImageUrl.setUrl(account.getImageUrl());
		}
		
		String date = DateTimeFormat.getFormat("MM-dd-yyyy").format(
				account.getLastLoginTime());
		lastLogin.setInnerText(date);
		cityLabel.setInnerText(capitalize(account.getCity()));
		stateLabel.setInnerText(capitalize(account.getState()));
		populateInterest();
	}

	// TODO
	void populateInterest() {
	}

	@UiHandler("editBasicInfoLink")
	void editCategory(ClickEvent event) {
		showAccountInfoFormWidget(AccountDetailsType.BASIC_INFO);
	}

	@UiHandler("editInterestLink")
	void editInterest(ClickEvent event) {
		showAccountInfoFormWidget(AccountDetailsType.INTEREST);
	}

	@UiHandler("editLocationLink")
	void editLocation(ClickEvent event) {
		showAccountInfoFormWidget();
	}

	void showAccountInfoFormWidget() {
		eadw.show(AccountDetailsType.BASIC_INFO);
	}

	void showAccountInfoFormWidget(AccountDetailsType tab) {
		eadw.show(tab);
	}

	boolean isAccountComplete() {
		return (account.getZip() == 0) || (account.getIntroduction() == null);
	}

	protected void displaySendMessageWidget(AccountDetails result) {
		// SendMessageWidget smw = new SendMessageWidget(service, eventBus,
		// this.getAd().account, this.currentAccount.account);
		// rootPanel.add(smw);
	}

	@UiHandler("sendMsgBtn")
	void sendMessage(ClickEvent event) {
		
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
	protected void internalOnUserLogin() {
	}
}
