package com.ziplly.app.client.view;

import java.util.Date;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.activities.AccountPresenter;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.client.widget.EditAccount;
import com.ziplly.app.client.widget.EditBusinessAccountWidget;
import com.ziplly.app.client.widget.SendMessageWidget;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.GetLatLngResult;
import com.ziplly.app.shared.ValidationResult;

public class BusinessAccountView extends Composite implements IAccountView<BusinessAccountDTO> {

	private static BusinessAccountViewUiBinder uiBinder = GWT
			.create(BusinessAccountViewUiBinder.class);

	interface BusinessAccountViewUiBinder extends UiBinder<Widget, BusinessAccountView> {
	}

	@UiField
	Alert message;
	
//	@UiField
//	LogoutWidget logoutWidget;
	
	@UiField
	Image profileImageUrl;
	
	@UiField
	Button sendMsgBtn;
	
	@UiField
	Element name;
	
	@UiField
	Anchor websiteUrl;
	
	@UiField
	Element phone;
	
	@UiField
	HTMLPanel tweetFormPanel;
	
	@UiField
	TextArea tweetTextBox;
	@UiField
	ControlGroup tweetCategoryCg;
	@UiField
	HelpInline tweetHelpInline;
	@UiField
	Button tweetBtn;
	@UiField
	ListBox tweetCategoryList;
	
	@UiField
	Anchor editAccountLink;

	private BusinessAccountDTO account;

	private AccountPresenter<BusinessAccountDTO> presenter;

	EditAccount<BusinessAccountDTO> editAccountView;
	
	public BusinessAccountView() {
		initWidget(uiBinder.createAndBindUi(this));
		editAccountView = new EditBusinessAccountWidget();
//		logoutWidget.setVisible(false);
		tweetCategoryList.clear();
	}

	@Override
	public void clear() {
		tweetTextBox.setText("");
		tweetCategoryCg.setType(ControlGroupType.NONE);
		tweetHelpInline.setText("");
		message.setVisible(false);
	}

//	@Override
//	public void displayLogoutWidget() {
//		logoutWidget.setVisible(true);
//	}

	@Override
	public void displayProfile(BusinessAccountDTO account) {
		this.account = account;
		if(account.getImageUrl() != null) {
			profileImageUrl.setUrl(account.getImageUrl());
		} else {
			profileImageUrl.setUrl(ZResources.IMPL.noImage().getSafeUri());
		}
		
		name.setInnerText(account.getName());
		if (account.getWebsite() != null) {
			websiteUrl.setHref(account.getWebsite());
			websiteUrl.setText("Website: "+account.getWebsite());
		}
		phone.setInnerText(account.getPhone());
		
		tweetCategoryList.clear();
		tweetCategoryList.addItem(TweetType.OFFERS.name());
		
		tweetFormPanel.getElement().getStyle().setDisplay(Display.INLINE);
		
//		logoutWidget.setVisible(true);
	}

	@Override
	public void displayPublicProfile(BusinessAccountDTO account) {
		displayProfile(account);
		tweetFormPanel.getElement().getStyle().setDisplay(Display.NONE);
		editAccountLink.setVisible(false);
	}

	@Override
	public void setPresenter(AccountPresenter<BusinessAccountDTO> presenter) {
		this.presenter = presenter;
		editAccountView.setPresenter(presenter);
//		logoutWidget.setPresenter(presenter);
	}

	@Override
	public void displayAccountUpdateSuccessfullMessage() {
		editAccountView.displayMessage(StringConstants.ACCOUNT_SAVE_SUCCESSFUL, AlertType.SUCCESS);
	}

	@Override
	public void displayAccountUpdateFailedMessage() {
		editAccountView.displayMessage(StringConstants.FAILED_TO_SAVE_ACCOUNT, AlertType.ERROR);
	}

	@Override
	public void clearTweet() {
		tweetTextBox.setText("");
		tweetCategoryCg.setType(ControlGroupType.NONE);
	}
	
	boolean validateTweet() {
		String tweet = tweetTextBox.getText();
		ValidationResult result = FieldVerifier.validateTweet(tweet);
		if (!result.isValid()) {
			tweetCategoryCg.setType(ControlGroupType.ERROR);
			tweetHelpInline.setText(result.getErrors().get(0).getErrorMessage());
			return false;
		}
		return true;
	}
	
	@UiHandler("tweetBtn")
	void tweet(ClickEvent event) {
		if (!validateTweet()) {
			return;
		}
		TweetDTO tweet = new TweetDTO();
		String content = tweetTextBox.getText().trim();
		tweet.setContent(content);
		tweet.setType(TweetType.OFFERS);
		tweet.setTimeCreated(new Date());
		presenter.tweet(tweet);
	}
	
	@UiHandler("editAccountLink")
	void editAccount(ClickEvent event) {
		editAccountView.display(account);
		editAccountView.show();
	}

	@Override
	public void closeSendMessageWidget() {
		// TODO Auto-generated method stub
		
	}

	SendMessageWidget smw;
	@UiHandler("sendMsgBtn")
	void sendMessage(ClickEvent event) {
		smw = new SendMessageWidget(account);
		smw.setPresenter(presenter);
		smw.show();
	}

	@Override
	public void displayTweets(List<TweetDTO> tweets) {
	}

	@Override
	public void displayMessage(String msg, AlertType type) {
		message.setText(msg);
		message.setType(type);
		message.setVisible(true);
	}

	@Override
	public com.google.gwt.user.client.Element getTweetSectionElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addTweets(List<TweetDTO> tweets) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displayLocationInMap(GetLatLngResult input) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateAccountDetails(ApplicationContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateComment(CommentDTO comment) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateTweetLike(LoveDTO like) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateTweet(TweetDTO tweet) {
		// TODO Auto-generated method stub
		
	}
	
}
