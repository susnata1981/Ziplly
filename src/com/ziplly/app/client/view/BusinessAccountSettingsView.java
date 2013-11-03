package com.ziplly.app.client.view;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.ziplly.app.client.activities.AccountSettingsPresenter;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class BusinessAccountSettingsView extends Composite implements ISettingsView<BusinessAccountDTO> {

	private static BusinessAccountSettingsViewUiBinder uiBinder = GWT
			.create(BusinessAccountSettingsViewUiBinder.class);

	interface BusinessAccountSettingsViewUiBinder extends
			UiBinder<Widget, BusinessAccountSettingsView> {
	}

	AccountSettingsPresenter<BusinessAccountDTO> presenter;

	@UiField
	Alert message;
	
	@UiField
	TextBox businessName;
	@UiField
	ControlGroup businessNameCg;
	@UiField
	HelpInline businessNameError;

	@UiField
	TextBox street1;
	@UiField
	ControlGroup street1Cg;
	@UiField
	HelpInline street1Error;

	@UiField
	TextBox street2;
	@UiField
	ControlGroup street2Cg;
	@UiField
	HelpInline street2Error;
	
	@UiField
	TextBox zip;
	@UiField
	ControlGroup zipCg;
	@UiField
	HelpInline zipError;

	@UiField
	TextBox website;
	@UiField
	ControlGroup websiteCg;
	@UiField
	HelpInline websiteError;
	
	@UiField
	TextBox email;
	@UiField
	ControlGroup emailCg;
	@UiField
	HelpInline emailError;
	
	@UiField
	FormPanel uploadForm;
	@UiField
	Button uploadBtn;
	@UiField
	Image profileImagePreview;
	
	@UiField
	Button saveBtn;
	@UiField
	Button cancelBtn;
	
	private BusinessAccountDTO account;
	
	public BusinessAccountSettingsView() {
		initWidget(uiBinder.createAndBindUi(this));
		message.setVisible(false);
		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);
	}
	
	@Override
	public void setPresenter(
			AccountSettingsPresenter<BusinessAccountDTO> presenter) {
		this.presenter = presenter;
	}

	@Override
	public void clear() {
		message.clear();
		message.setVisible(false);
		businessName.setText("");
		street1.setText("");
		street2.setText("");
		zip.setText("");
		website.setText("");
		email.setText("");
	}

	@Override
	public void displaySettings(BusinessAccountDTO account) {
		this.account = account;
		resetUploadForm();
		if (account != null) {
			businessName.setText(account.getName());
			street1.setText(account.getStreet1());
			street2.setText(account.getStreet2());
			zip.setText(Integer.toString(account.getZip()));
			website.setText(account.getWebsite());
			email.setText(account.getEmail());
			if (account.getImageUrl() != null) {
				displayImagePreview(account.getImageUrl());
			}
		}
	}

	boolean validateZip() {
		String zipInput = zip.getText().trim();
		ValidationResult validateZip = FieldVerifier.validateZip(zipInput);
		if (!validateZip.isValid()) {
			zipCg.setType(ControlGroupType.ERROR);
			zipError.setText(validateZip.getErrors().get(0).getErrorMessage());
			zipError.setVisible(true);
			return false;
		}
		return true;
	}

	boolean validateName(String name, ControlGroup cg, HelpInline helpInline) {
		ValidationResult result = FieldVerifier.validateName(name);
		if (!result.isValid()) {
			cg.setType(ControlGroupType.ERROR);
			helpInline.setText(result.getErrors().get(0).getErrorMessage());
			helpInline.setVisible(true);
			return false;
		}
		return true;
	}

	boolean validateEmail() {
		String emailInput = email.getText().trim();
		ValidationResult result = FieldVerifier.validateEmail(emailInput);
		if (!result.isValid()) {
			emailCg.setType(ControlGroupType.ERROR);
			emailError.setText(result.getErrors().get(0).getErrorMessage());
			emailError.setVisible(true);
			return false;
		}
		return true;
	}

	boolean validatePassword(String password, ControlGroup cg,
			HelpInline helpInline) {
		ValidationResult result = FieldVerifier.validatePassword(password);
		if (!result.isValid()) {
			cg.setType(ControlGroupType.ERROR);
			helpInline.setText(result.getErrors().get(0).getErrorMessage());
			helpInline.setVisible(true);
			return false;
		}
		return true;
	}

	boolean validateInput() {
		String businessNameInput = businessName.getText().trim();
		boolean valid = true;
		valid &= validateName(businessNameInput, businessNameCg, businessNameError);

		String lastnameInput = street1.getText().trim();
		valid &= validateName(lastnameInput, street1Cg, street1Error);

		valid &= validateEmail();

		valid &= validateZip();

		return valid;
	}
	
	void resetErrors() {
		businessNameCg.setType(ControlGroupType.NONE);
		businessNameError.setVisible(false);
		street1Cg.setType(ControlGroupType.NONE);
		street1Error.setVisible(false);
		zipCg.setType(ControlGroupType.NONE);
		zipError.setVisible(false);
		emailCg.setType(ControlGroupType.NONE);
		emailError.setVisible(false);
	}
	
	@Override
	public void onSave() {
		resetErrors();
		if (!validateInput()) {
			return;
		}
		
		String name = businessName.getText().trim();
		String streetOne = street1.getText().trim();
		String streetTwo = street2.getText().trim();
		String websiteUrl = website.getText().trim();
		String emailInput = email.getText().trim();
		String zipInput = zip.getText().trim();
		String imageUrl = profileImagePreview.getUrl();
		BusinessAccountDTO acct = new BusinessAccountDTO();
		acct.setAccountId(account.getAccountId());
		acct.setUid(account.getUid());
		acct.setName(name);
		acct.setStreet1(streetOne);
		acct.setStreet2(streetTwo);
		acct.setZip(Integer.parseInt(zipInput));
		acct.setWebsite(websiteUrl);
		acct.setEmail(emailInput);
		if (imageUrl != null) {
			acct.setImageUrl(imageUrl);
		}
		presenter.save(acct);
	}

	@Override
	public void setUploadFormActionUrl(String imageUrl) {
		uploadForm.setAction(imageUrl);
		uploadBtn.setEnabled(true);
	}

	@Override
	public void setUploadFormSubmitCompleteHandler(SubmitCompleteHandler submitCompleteHandler) {
		uploadForm.addSubmitCompleteHandler(submitCompleteHandler);
	}

	@Override
	public void clearError() {
		message.setVisible(false);
	}

	@Override
	public void displayMessage(String msg, AlertType type) {
		message.setType(type);
		message.setText(msg);
		message.setVisible(true);
	}

	@UiHandler("saveBtn")
	public void save(ClickEvent event) {
		onSave();
	}

	@UiHandler("cancelBtn")
	public void cancel(ClickEvent event) {
		onCancel();
	}

	@Override
	public void onCancel() {
		presenter.cancel();
	}

	@Override
	public void displayImagePreview(String imageUrl) {
		profileImagePreview.setUrl(imageUrl);
	}

	@Override
	public void resetUploadForm() {
		uploadBtn.setEnabled(false);
		uploadForm.setAction("");
	}
	
	@Override
	public void onUpload() {
		uploadForm.submit();
	}
	
	@UiHandler("uploadBtn")
	void uploadImage(ClickEvent event) {
		onUpload();
	}
}
