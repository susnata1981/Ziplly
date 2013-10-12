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
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.widget.LoginWidget;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.RegisterAccountAction;
import com.ziplly.app.shared.RegisterAccountResult;
import com.ziplly.app.shared.ValidationResult;

public class SignupView extends AbstractView {
	private static final String PASSWORD_MISMATCH_ERROR = "Password & Confirm Password doesn't match";
	private static SignupViewUiBinder uiBinder = GWT
			.create(SignupViewUiBinder.class);

	interface SignupViewUiBinder extends UiBinder<Widget, SignupView> {
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

	@UiField(provided = true)
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
	
	@Inject
	public SignupView(CachingDispatcherAsync dispatcher, SimpleEventBus eventBus) {
		super(dispatcher, eventBus);
		System.out.println("Dispatcher = "+dispatcher+" eventBus="+eventBus);
	}

	@Override
	protected void initWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);
		uploadBtn.setEnabled(false);
		uploadForm.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				resetUploadForm();
				String imageUrl = event.getResults();
				profileImagePreview.setUrl(imageUrl);
				profileImagePreview.setVisible(true);
				SignupView.this.profileImageUrl = imageUrl;
			}
		});
	}

	@Override
	protected void postInitWidget() {
		// resetForm();
		infoField.setVisible(false);
		resetUploadForm();
	}

	private void resetUploadForm() {
		uploadForm.reset();
		profileImagePreview.setVisible(false);
		service.getUploadUrl(new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(String result) {
				System.out.println("result1:"+result);
				result = result.replace("susnatas-MacBook-Pro.local:8888", "127.0.0.1:8888");
				System.out.println("result2:"+result);
				uploadForm.setAction(result);
				uploadBtn.setEnabled(true);
			}
		});
	}
	
	@Override
	protected void setupUiElements() {
		loginWidget = new LoginWidget(dispatcher, eventBus);
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
	
	boolean validatePassword(String password, ControlGroup cg, HelpInline helpInline) {
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
		valid &= validatePassword(confirmPasswordInput, confirmPasswordCg, confirmPasswordError);
		
		if (passwordInput != null && confirmPasswordInput != null) {
			if (!confirmPasswordInput.equals(passwordInput)) {
				passwordCg.setType(ControlGroupType.ERROR);
				passwordError.setText(PASSWORD_MISMATCH_ERROR);
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
		AccountDTO account = new AccountDTO();
		account.setFirstName(firstnameInput);
		account.setLastName(lastnameInput);
		account.setEmail(emailInput);
		account.setPassword(password.getText().trim());
		account.setZip(Integer.parseInt(zipInput));
		account.setLastLoginTime(new Date());
		account.setTimeCreated(new Date());
		
		if (profileImageUrl != null) {
			account.setImageUrl(profileImageUrl);
		}
		
		dispatcher.execute(new RegisterAccountAction(account), new DispatcherCallbackAsync<RegisterAccountResult>() {
			@Override
			public void onSuccess(RegisterAccountResult result) {
				System.out.println("Account "+result.getAccount()+" registered.");
				eventBus.fireEvent(new LoginEvent(result.getAccount()));
			}
		});
	}

	public void displayAccount(AccountDTO account) {
		resetForm();
		firstname.setText(account.getFirstName());
		lastname.setText(account.getLastName());
		email.setText(account.getEmail());
		zip.setText(Integer.toString(account.getZip()));
		System.out.println("Img url="+account.getImageUrl());
		profileImagePreview.setUrl(account.getImageUrl());
		profileImagePreview.setVisible(true);
	}
	
	@UiHandler("uploadBtn")
	void upload(ClickEvent event) {
		uploadForm.submit();
	}
}