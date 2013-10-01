package com.ziplly.app.client.view;

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
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.model.AccountDTO;

public class SignupView extends AbstractView {
	private static final String PASSWORD_MISMATCH_ERROR = "Password & Confirm Password doesn't match";
	private static final String CANT_BE_EMPTY = "Can't be empty";
	private static final String INVALID_ZIP = "Invalid zip";
	private static final String INVALID_EMAIL = "Invalid email";
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

	public SignupView(SimpleEventBus eventBus) {
		super(eventBus);
	}

	@Override
	protected void initWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	protected void postInitWidget() {
		resetForm();
	}

	@Override
	protected void setupUiElements() {
	}

	RegExp zipPattern = RegExp.compile("(\\d+){3,5}");

	boolean validateZip() {
		String zipInput = zip.getText().trim();
		if (zipInput == null || zipInput.equals("")) {
			zipCg.setType(ControlGroupType.ERROR);
			zipError.setText(CANT_BE_EMPTY);
			zipError.setVisible(true);
			return false;
		}

		MatchResult matcher = zipPattern.exec(zipInput);
		if (matcher == null) {
			zipCg.setType(ControlGroupType.ERROR);
			zipError.setText(INVALID_ZIP);
			zipError.setVisible(true);
			return false;
		}
		return true;
	}

	boolean validateInput() {
		String firstnameInput = firstname.getText().trim();
		boolean valid = true;
		if (firstnameInput == null || firstnameInput.equals("")) {
			firstnameCg.setType(ControlGroupType.ERROR);
			firstnameError.setText(CANT_BE_EMPTY);
			firstnameError.setVisible(true);
			valid = false;
		}

		String lastnameInput = lastname.getText().trim();
		if (lastnameInput == null || lastnameInput.equals("")) {
			lastnameCg.setType(ControlGroupType.ERROR);
			lastnameError.setText(CANT_BE_EMPTY);
			lastnameError.setVisible(true);
			valid = false;
		}

		boolean result = validateEmail();
		if (!result) {
			valid = false;
		}

		result = validateZip();
		if (!result) {
			valid = false;
		}

		String passwordInput = password.getText().trim();
		if (passwordInput == null || passwordInput.equals("")) {
			passwordCg.setType(ControlGroupType.ERROR);
			passwordError.setText(CANT_BE_EMPTY);
			passwordError.setVisible(true);
			valid = false;
		}

		String confirmPasswordInput = confirmPassword.getText().trim();
		if (confirmPasswordInput == null || confirmPasswordInput.equals("")) {
			confirmPasswordCg.setType(ControlGroupType.ERROR);
			confirmPasswordError.setText(CANT_BE_EMPTY);
			confirmPasswordError.setVisible(true);
			valid = false;
		}

		if (!passwordError.isVisible()) {
			if (passwordInput != null && confirmPasswordInput != null) {
				if (!confirmPasswordInput.equals(passwordInput)) {
					passwordCg.setType(ControlGroupType.ERROR);
					passwordError.setText(PASSWORD_MISMATCH_ERROR);
					passwordError.setVisible(true);
				}
			}
		}
		
		return valid;
	}

	RegExp emailPattern = RegExp.compile("\\w+@[a-z]+\\.[a-z]{2,3}");

	boolean validateEmail() {
		String emailInput = email.getText().trim();
		if (emailInput == null || emailInput.equals("")) {
			emailCg.setType(ControlGroupType.ERROR);
			emailError.setText(CANT_BE_EMPTY);
			emailError.setVisible(true);
			return false;
		}

		MatchResult matcher = emailPattern.exec(emailInput);
		if (matcher == null) {
			emailCg.setType(ControlGroupType.ERROR);
			emailError.setText(INVALID_EMAIL);
			emailError.setVisible(true);
			return false;
		}
		return true;
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
		account.setZip(Integer.parseInt(zipInput));
		service.register(account, new AsyncCallback<AccountDTO>() {
			@Override
			public void onSuccess(AccountDTO account) {
				eventBus.fireEvent(new LoginEvent(account));
			}

			@Override
			public void onFailure(Throwable caught) {
				infoField.setType(AlertType.ERROR);
			}
		});
	}
}