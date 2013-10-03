package com.ziplly.app.client.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Image;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.ziplly.app.client.view.AbstractAccountView;
import com.ziplly.app.client.view.event.AccountUpdateEvent;
import com.ziplly.app.client.view.handler.AccountUpdateEventHandler;
import com.ziplly.app.client.widget.cell.InterestDetailsMiniCell;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountDetails;
import com.ziplly.app.model.CategoryDetails;

public class AccountWidget extends AbstractAccountView {

	private static AccountWidgetUiBinder uiBinder = GWT
			.create(AccountWidgetUiBinder.class);
	private Logger logger = Logger.getLogger("AccountWidget");

	interface AccountWidgetUiBinder extends UiBinder<Widget, AccountWidget> {
	}

	interface Style extends CssResource {
		String interest();
	}

	@UiField
	Image profileImageUrl;

	@UiField
	Element name;

	@UiField
	Element introduction;

	@UiField
	Anchor editCategoryLink;

	@UiField
	HTMLPanel categoryList;

	@UiField
	Element lastLogin;

	@UiField
	Element cityLabel;

	@UiField
	Element stateLabel;

	@UiField(provided = true)
	CellList<CategoryDetails> interestList;

	ListDataProvider<CategoryDetails> dataProvider;

	@UiField
	Style style;

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
	
	@UiFactory
	public MyBundle createTheBundle() {
		MyBundle.INSTANCE.style().ensureInjected();
		return MyBundle.INSTANCE;
	}

	private AccountInfoFormWidget aifw;

	private boolean displayEdit;

	public AccountWidget(SimpleEventBus eventBus,
			boolean displayEdit) {
		
		super(eventBus);
		this.displayEdit = displayEdit;
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
		aifw = new AccountInfoFormWidget(eventBus);
		interestList = new CellList<CategoryDetails>(
				new InterestDetailsMiniCell());
	}

	@Override
	protected void setupHandlers() {
		eventBus.addHandler(AccountUpdateEvent.TYPE,
				new AccountUpdateEventHandler() {
					@Override
					public void onEvent(AccountUpdateEvent event) {
//						AccountWidget.this.currentAccount = event.getAccountDetails();
//						Account a = AccountWidget.this.currentAccount.account;
//						System.out.println("Account:" + a.getDisplayName()
//								+ " last login:" + a.getLastLoginTime());
//						updateUiWithProfileData();
					}
				});
	}

	public void displayAccount(AccountDTO account) {
		this.account = account;
		updateUiWithProfileData();
		if (!isAccountComplete()) {
			showAccountInfoFormWidget();
		}
	}

	private void updateUiWithProfileData() {
		// setup account info
		updateProfile();

		// setup category list
		updateInterests();
		logger.log(Level.FINE, "Finishd loading account widget");
	}

	void updateProfile() {
		name.setInnerText(account.getDisplayName());
		introduction.setInnerText(account.getIntroduction());
		profileImageUrl.setUrl(account.getImageUrl());
		String date = DateTimeFormat.getFormat("MM-dd-yyyy").format(
				account.getLastLoginTime());
		lastLogin.setInnerText(date);

//		cityLabel.setInnerText(capitalize(account.getCity()));
//		stateLabel.setInnerText(capitalize(account.getState()));

		if (!displayEdit) {
			editCategoryLink.setVisible(false);
		}
	}

	void updateInterests() {
		List<CategoryDetails> cdList = new ArrayList<CategoryDetails>();
//		for (Category c : currentAccount.categories) {
//			CategoryDetails cd = new CategoryDetails();
//			cd.category = c;
//			cdList.add(cd);
//		}
		dataProvider = new ListDataProvider<CategoryDetails>();
		dataProvider.addDataDisplay(interestList);
		interestList.setRowData(cdList);
	}

	@UiHandler("editCategoryLink")
	void editCategory(ClickEvent event) {
		showAccountInfoFormWidget();
	}

	void showAccountInfoFormWidget() {
		aifw.populateInterestPanel();
		aifw.show();
	}

	boolean isAccountComplete() {
		return (account.getZip() == 0)
				|| (account.getIntroduction() == null);
	}

	void isUserLoggedIn() {
//		service.getLoggedInUser(new AsyncCallback<AccountDetails>() {
//			@Override
//			public void onSuccess(AccountDetails result) {
//				AccountWidget.this.displaySendMessageWidget(result);
//			}
//			
//			@Override
//			public void onFailure(Throwable caught) {
//				logger.log(Level.SEVERE, "Error sending message: "+caught.getMessage());
//				Window.alert("Couldn't send message at this time");
//			}
//		});
	}
	
	protected void displaySendMessageWidget(AccountDetails result) {
//		SendMessageWidget smw = new SendMessageWidget(service, eventBus, this.getAd().account, this.currentAccount.account);
//		rootPanel.add(smw);
	}

	@UiHandler("sendMsgBtn")
	void sendMessage(ClickEvent event) {
		isUserLoggedIn();
	}
	
	
//	private class UrlHandler implements ClickHandler {
//		private Category category;
//
//		public UrlHandler(Category category) {
//			this.category = category;
//		}
//
//		@Override
//		public void onClick(ClickEvent event) {
//			History.newItem("category/"
//					+ category.getCategoryType().toLowerCase());
//		}
//	}

	public void setIntroduction(String introduction2) {
		introduction.setInnerText(introduction2);
	}

	String capitalize(String str) {
		if (str == null || str.length() == 0) {
			throw new IllegalArgumentException();
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
