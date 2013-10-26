package com.ziplly.app.client.view;

import java.util.Date;

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
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.AccountPresenter;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.client.widget.EditAccount;
import com.ziplly.app.client.widget.EditBusinessAccountWidget;
import com.ziplly.app.client.widget.LogoutWidget;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class BusinessAccountView extends Composite implements IAccountView<BusinessAccountDTO> {

	private static BusinessAccountViewUiBinder uiBinder = GWT
			.create(BusinessAccountViewUiBinder.class);

	interface BusinessAccountViewUiBinder extends UiBinder<Widget, BusinessAccountView> {
	}

	@UiField
	LogoutWidget logoutWidget;
	
	@UiField
	Image profileImageUrl;
	
	@UiField
	FormPanel imageUploadForm;
	@UiField
	Button uploadBtn;
	@UiField
	Button saveImageBtn;
	
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
		logoutWidget.setVisible(false);
		imageUploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		imageUploadForm.setMethod(FormPanel.METHOD_POST);
	}

	@Override
	public void clear() {
		tweetTextBox.setText("");
		tweetCategoryCg.setType(ControlGroupType.NONE);
		tweetHelpInline.setText("");
		uploadBtn.setEnabled(false);
		saveImageBtn.setVisible(false);
	}

	@Override
	public void displayLogoutWidget() {
		logoutWidget.setVisible(true);
	}

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
		
		tweetFormPanel.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		
		logoutWidget.setVisible(true);
		uploadBtn.setEnabled(false);
		saveImageBtn.setVisible(false);
	}

	@Override
	public void displayPublicProfile(BusinessAccountDTO account) {
		displayProfile(account);
		imageUploadForm.setVisible(false);
		tweetFormPanel.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		editAccountLink.setVisible(false);
	}

	@Override
	public void setPresenter(AccountPresenter<BusinessAccountDTO> presenter) {
		this.presenter = presenter;
		editAccountView.setPresenter(presenter);
		logoutWidget.setPresenter(presenter);
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
		TweetType tweetType = TweetType.values()[tweetCategoryList
				.getSelectedIndex()];
		tweet.setType(tweetType);
		tweet.setTimeCreated(new Date());
		presenter.tweet(tweet);
	}
	
	@UiHandler("editAccountLink")
	void editAccount(ClickEvent event) {
		editAccountView.display(account);
		editAccountView.show();
	}

	@Override
	public void setImageUploadUrl(String url) {
		imageUploadForm.setAction(url);
		uploadBtn.setEnabled(true);
	}

	@Override
	public void addUploadFormHandler(SubmitCompleteHandler submitCompleteHandler) {
		imageUploadForm.addSubmitCompleteHandler(submitCompleteHandler);
	}

	@Override
	public void displayProfileImagePreview(String imageUrl) {
		profileImageUrl.setUrl(imageUrl);
		saveImageBtn.setVisible(true);
	}

	@Override
	public void resetUploadForm() {
		imageUploadForm.reset();
		uploadBtn.setEnabled(false);
	}
	
	@UiHandler("uploadBtn") 
	void uploadImage(ClickEvent event) {
		saveImageBtn.setVisible(false);
		imageUploadForm.submit();
	}
	
	@UiHandler("saveImageBtn")
	void saveImage(ClickEvent event) {
		this.account.setImageUrl(profileImageUrl.getUrl());
		presenter.save(account);
		saveImageBtn.setVisible(false);
	}
}
