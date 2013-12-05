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
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.ziplly.app.client.activities.SignupActivityPresenter;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.client.widget.LoginWidget;
import com.ziplly.app.model.AccountStatus;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.Role;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class SignupView extends Composite implements
		ISignupView<SignupActivityPresenter>, LoginAwareView {
//	public static final String PASSWORD_MISMATCH_ERROR = "Password & Confirm Password doesn't match";
	private static SignupViewUiBinder uiBinder = GWT
			.create(SignupViewUiBinder.class);

	interface SignupViewUiBinder extends UiBinder<Widget, SignupView> {
	}

	@UiFactory
	ZResources resources() {
		ZResources.IMPL.style().ensureInjected();
		return ZResources.IMPL;
	}
	
	@UiField
	TextBox firstname;
	@UiField
	ControlGroup firstnameCg;
	@UiField
	HelpInline firstnameError;

	@UiField
	TextBox lastname;
	@UiField
	ControlGroup lastnameCg;
	@UiField
	HelpInline lastnameError;

	@UiField
	TextBox email;
	@UiField
	ControlGroup emailCg;
	@UiField
	HelpInline emailError;

	@UiField
	TextBox zip;
	@UiField
	ControlGroup zipCg;
	@UiField
	HelpInline zipError;

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
	
	boolean imageUploaded = false;
	String profileImageUrl;
	SignupActivityPresenter presenter;

	@Inject
	public SignupView() {
		initWidget(uiBinder.createAndBindUi(this));
		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);
		profileImagePreview.setUrl(ZResources.IMPL.noImage().getSafeUri());
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

	boolean checkEmailInvitationStatus = false;
	
	public void verifiedEmailInvitationStatus() {
		checkEmailInvitationStatus = true;
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
		
//		if (isRegistrationRescricted) {
//			String code = Window.Location.getParameter("code");
//			presenter.verifyInvitationForEmail(FieldVerifier.sanitize(email.getText()), code);
//			while (!checkEmailInvitationStatus) {
//				
//			}
//		}
		
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
		String firstnameInput = firstname.getText().trim();
		boolean valid = true;
		valid &= validateName(firstnameInput, firstnameCg, firstnameError);

		String lastnameInput = lastname.getText().trim();
		valid &= validateName(lastnameInput, lastnameCg, lastnameError);

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
		firstname.setText("");
		lastname.setText("");
		email.setText("");
		password.setText("");
		confirmPassword.setText("");
	}

	void resetErrors() {
		firstnameCg.setType(ControlGroupType.NONE);
		firstnameError.setVisible(false);
		lastnameCg.setType(ControlGroupType.NONE);
		lastnameError.setVisible(false);
		zipCg.setType(ControlGroupType.NONE);
		zipError.setVisible(false);
		emailCg.setType(ControlGroupType.NONE);
		emailError.setVisible(false);
	}

	@UiHandler("signupBtn")
	void signup(ClickEvent event) {
		System.out.println("Calling submit signup...");
		resetErrors();
		if (!validateInput()) {
			return;
		}
		infoField.setType(AlertType.SUCCESS);
		String firstnameInput = firstname.getText().trim();
		String lastnameInput = lastname.getText().trim();
		String emailInput = email.getText().trim();
		String zipInput = zip.getText().trim();
		PersonalAccountDTO account = new PersonalAccountDTO();
		account.setFirstName(firstnameInput);
		account.setLastName(lastnameInput);
		account.setStatus(AccountStatus.PENDING_ACTIVATION);
		account.setEmail(emailInput);
		account.setPassword(password.getText().trim());
		account.setZip(Integer.parseInt(zipInput));
		account.setRole(Role.USER);
		account.setLastLoginTime(new Date());
		account.setTimeCreated(new Date());

		if (imageUploaded && profileImageUrl != null) {
			account.setImageUrl(profileImageUrl);
		} 

		String value = System.getProperty(StringConstants.RESTRICT_REGISTRATION_FEATURE, "false");
		boolean isRegistrationRescricted = Boolean.valueOf(value);
		if (isRegistrationRescricted) {
			System.out.println("Window.Loc="+Window.Location.getParameter("gwt.codesvr"));
			String code = Window.Location.getParameter("code");
			presenter.register(account, code);
		} else {
			presenter.register(account);
		}
	}

	public void displayAccount(PersonalAccountDTO account) {
		resetForm();
		firstname.setText(account.getFirstName());
		lastname.setText(account.getLastName());
		email.setText(account.getEmail());
		
		zip.setText(Integer.toString(account.getZip()));
		
		if (account.getImageUrl() != null) {
			this.profileImageUrl = account.getImageUrl();
			profileImagePreview.setUrl(account.getImageUrl());
			profileImagePreview.setVisible(true);
		}
	}

	@UiHandler("uploadBtn")
	void upload(ClickEvent event) {
		uploadForm.submit();
		imageUploaded = true;
	}

	@Override
	public void displayLoginErrorMessage(String msg, AlertType type) {
		loginWidget.displayMessage(msg, type);
	}

	@Override
	public void resetLoginForm() {
		loginWidget.resetLoginForm();
	}

	@Override
	public void clear() {
		resetForm();
		loginWidget.clear();
	}

	@Override
	public void setPresenter(SignupActivityPresenter presenter) {
		this.presenter = (SignupActivityPresenter) presenter;
		loginWidget.setPresenter(presenter);
	}

	public void setImageUploadUrl(String imageUrl) {
		uploadForm.setAction(imageUrl);
		uploadBtn.setEnabled(true);
	}

	public void addUploadFormHandler(SubmitCompleteHandler submitCompleteHandler) {
		uploadForm.addSubmitCompleteHandler(submitCompleteHandler);
	}

	public void displayMessage(String msg, AlertType type) {
//		loginWidget.displayMessage(msg, error);
		infoField.setText(msg);
		infoField.setType(type);
		infoField.setVisible(true);
	}
}