package com.ziplly.app.client.widget;

import java.io.UnsupportedEncodingException;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.ziplly.app.client.activities.LoginPresenter;
import com.ziplly.app.client.oauth.OAuthConfig;
import com.ziplly.app.client.oauth.OAuthFactory;
import com.ziplly.app.client.oauth.OAuthProvider;
import com.ziplly.app.client.places.BusinessSignupPlace;
import com.ziplly.app.client.places.PasswordRecoveryPlace;
import com.ziplly.app.client.places.SignupPlace;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.client.view.ILoginAccountView;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class LoginWidget extends Composite implements ILoginAccountView<LoginPresenter> {

	private static LoginWidgetUiBinder uiBinder = GWT.create(LoginWidgetUiBinder.class);

	interface LoginWidgetUiBinder extends UiBinder<Widget, LoginWidget> {
	}

	public static final String ACCOUNT_DOES_NOT_EXIST = "Account with this email doesn't exist";
	public static final String INVALID_ACCOUNT_CREDENTIALS = "Invalid account credentials";
	public static final String ACCOUNT_NOT_ACTIVE = "Please verify your email, account isn't active";
	private OAuthConfig authConfig;

	@Inject
	public LoginWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		message.setVisible(false);
		setWidth("90%");
	}

	@UiFactory
	ZResources resources() {
		ZResources.IMPL.style().ensureInjected();
		return ZResources.IMPL;
	}

	public void setWidth(String width) {
		loginWidgetPanel.setWidth(width);
	}

	public void setResidentAccountSignupLink(String display) {
		Boolean value = Boolean.valueOf(display);
		if (!value.booleanValue()) {
			residentSignupPanel.getElement().getStyle().setDisplay(Display.NONE);
		}
	}

	public void setBusinessAccountSignupLink(boolean display) {
		Boolean value = Boolean.valueOf(display);
		if (!value.booleanValue()) {
			businessSignupPanel.getElement().getStyle().setDisplay(Display.NONE);
		}
	}

	@UiField
	HTMLPanel loginWidgetPanel;

	@UiField
	TextBox email;
	@UiField
	ControlGroup emailCg;
	@UiField
	HelpInline emailError;

	@UiField
	TextBox password;
	@UiField
	ControlGroup passwordCg;
	@UiField
	HelpInline passwordError;

	@UiField
	com.google.gwt.user.client.ui.Button fbLoginBtn;

	@UiField
	Button loginBtn;

	@UiField
	Alert message;

	@UiField
	Anchor residentSignupLink;
	@UiField
	HTMLPanel residentSignupPanel;

	@UiField
	Anchor businessSignupLink;
	@UiField
	HTMLPanel businessSignupPanel;

	@UiField
	Anchor passwordRecoveryLink;

	private LoginPresenter presenter;

	@UiHandler("fbLoginBtn")
	void fbLogin(ClickEvent event) {
		try {
			if (authConfig == null) {
				authConfig =
				    OAuthFactory.getAuthConfig(OAuthProvider.FACEBOOK.name(), presenter.getEnvironment());
			}
			Window.Location.replace(authConfig.getAuthorizationUrl());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		fbLoginBtn.setEnabled(false);
	}

	boolean validateInput() {
		String emailInput = email.getText();
		ValidationResult result = FieldVerifier.validateEmail(emailInput);
		boolean valid = true;

		if (!result.isValid()) {
			emailError.setText(result.getErrors().get(0).getErrorMessage());
			emailCg.setType(ControlGroupType.ERROR);
			emailError.setVisible(true);
			valid = false;
		}

		String passwordInput = password.getText();
		result = FieldVerifier.validateString(passwordInput, FieldVerifier.MAX_PASSWORD_LENGTH);

		if (!result.isValid()) {
			passwordError.setText(result.getErrors().get(0).getErrorMessage());
			passwordCg.setType(ControlGroupType.ERROR);
			passwordError.setVisible(true);
			valid = false;
		}

		return valid;
	}

	@UiHandler("loginBtn")
	void login(ClickEvent event) {
		clearError();
		resetMessage();
		if (!validateInput()) {
			return;
		}

		String emailInput = FieldVerifier.getEscapedText(email.getText());
		String passwordInput = password.getText().trim();
		presenter.onLogin(emailInput, passwordInput);
		loginBtn.setEnabled(false);
	}

	@Override
	public void clear() {
		resetLoginForm();
		resetMessage();
		loginBtn.setEnabled(true);
		fbLoginBtn.setEnabled(true);
	}

	@Override
	public void displayMessage(String msg, AlertType type) {
		message.setText(msg);
		message.setType(type);
		message.setVisible(true);
	}

	@Override
	public void resetLoginForm() {
		clearError();
		email.setText("");
		password.setText("");
		loginBtn.setEnabled(true);
		fbLoginBtn.setEnabled(true);
	}

	public void clearError() {
		emailError.setText("");
		emailCg.setType(ControlGroupType.NONE);
		passwordError.setText("");
		passwordCg.setType(ControlGroupType.NONE);
	}

	@Override
	public void resetMessage() {
		message.setVisible(false);
		message.clear();
	}

	@Override
	public void setPresenter(LoginPresenter presenter) {
		this.presenter = presenter;
	}

	@UiHandler("residentSignupLink")
	public void residentSignupLinkClicked(ClickEvent event) {
		presenter.goTo(new SignupPlace());
	}

	@UiHandler("businessSignupLink")
	public void businessSignupLinkClicked(ClickEvent event) {
		presenter.goTo(new BusinessSignupPlace(""));
	}

	@UiHandler("passwordRecoveryLink")
	public void passwordRecoveryLinkClicked(ClickEvent event) {
		presenter.goTo(new PasswordRecoveryPlace());
	}
}
