package com.ziplly.app.client.view;

import java.util.Date;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.PasswordTextBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.ziplly.app.client.activities.SignupActivityPresenter;
import com.ziplly.app.client.widget.LoginWidget;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class BusinessSignupView extends Composite implements ISignupView<SignupActivityPresenter> {
	private static BusinessSignupViewUiBinder uiBinder = GWT
			.create(BusinessSignupViewUiBinder.class);

	interface BusinessSignupViewUiBinder extends
			UiBinder<Widget, BusinessSignupView> {
	}

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
	PasswordTextBox password;
	@UiField
	ControlGroup passwordCg;
	@UiField
	HelpInline passwordError;

	@UiField
	PasswordTextBox confirmPassword;
	@UiField
	ControlGroup confirmPasswordCg;
	@UiField
	HelpInline confirmPasswordError;

	@UiField
	Alert infoField;

	@UiField
	Button signupBtn;

	@UiField
	LoginWidget loginWidget;

	@UiField
	FileUpload uploadField;

	@UiField
	FormPanel uploadForm;

	@UiField
	Button uploadBtn;

	@UiField
	Image profileImagePreview;

	String profileImageUrl;
	SignupActivityPresenter presenter;

	@Inject
	public BusinessSignupView() {
		initWidget(uiBinder.createAndBindUi(this));
		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);
	}

	public void reset() {
		uploadForm.reset();
		uploadBtn.setEnabled(false);
		infoField.setVisible(false);
	}

	public void hideProfileImagePreview() {
		profileImagePreview.setVisible(false);
	}

	public void displayProfileImagePreview(String imageUrl) {
		profileImagePreview.setUrl(imageUrl);
		profileImagePreview.setVisible(true);
		this.profileImageUrl = imageUrl;
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

		String passwordInput = password.getText().trim();
		valid &= validatePassword(passwordInput, passwordCg, passwordError);

		String confirmPasswordInput = confirmPassword.getText().trim();
		valid &= validatePassword(confirmPasswordInput, confirmPasswordCg,
				confirmPasswordError);

		if (passwordInput != null && confirmPasswordInput != null) {
			if (!confirmPasswordInput.equals(passwordInput)) {
				passwordCg.setType(ControlGroupType.ERROR);
				passwordError.setText(StringConstants.PASSWORD_MISMATCH_ERROR);
				passwordError.setVisible(true);
			}
		}
		return valid;
	}

	void resetForm() {
		resetFormFields();
		resetErrors();
		infoField.setVisible(false);
	}

	void resetFormFields() {
		businessName.setText("");
		street1.setText("");
		email.setText("");
		password.setText("");
		confirmPassword.setText("");
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

	@UiHandler("signupBtn")
	void signup(ClickEvent event) {
		resetErrors();
		if (!validateInput()) {
			return;
		}
		infoField.setType(AlertType.SUCCESS);
		String name = businessName.getText().trim();
		String streetOne = street1.getText().trim();
		String streetTwo = street2.getText().trim();
		String websiteUrl = website.getText().trim();
		String emailInput = email.getText().trim();
		String zipInput = zip.getText().trim();
		BusinessAccountDTO account = new BusinessAccountDTO();
		account.setName(name);
		account.setStreet1(streetOne);
		account.setStreet2(streetTwo);
		account.setZip(Integer.parseInt(zipInput));
		account.setWebsite(websiteUrl);
		account.setEmail(emailInput);
		account.setPassword(password.getText().trim());
		account.setLastLoginTime(new Date());
		account.setTimeCreated(new Date());

		if (profileImageUrl != null) {
			account.setImageUrl(profileImageUrl);
		}

		presenter.register(account);
	}

	@UiHandler("uploadBtn")
	void upload(ClickEvent event) {
		uploadForm.submit();
	}

	@Override
	public void clear() {
		resetForm();
		resetLoginForm();
	}

	public void setImageUploadUrl(String imageUrl) {
		uploadForm.setAction(imageUrl);
		uploadBtn.setEnabled(true);
	}

	public void addUploadFormHandler(SubmitCompleteHandler submitCompleteHandler) {
		uploadForm.addSubmitCompleteHandler(submitCompleteHandler);
	}

	@Override
	public void setPresenter(SignupActivityPresenter presenter) {
		this.presenter = presenter;
		loginWidget.setPresenter(presenter);
	}

	@Override
	public void displayAccount(PersonalAccountDTO a) {
		throw new RuntimeException();
	}

	@Override
	public void displayMessage(String msg, AlertType type) {
		loginWidget.displayMessage(msg, type);
	}

	@Override
	public void resetLoginForm() {
		loginWidget.clear();
	}
}
