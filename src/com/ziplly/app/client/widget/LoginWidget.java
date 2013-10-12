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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.exceptions.InvalidCredentialsException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.oauth.OAuthConfig;
import com.ziplly.app.client.oauth.OAuthFactory;
import com.ziplly.app.client.oauth.OAuthProvider;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidateLoginAction;
import com.ziplly.app.shared.ValidateLoginResult;
import com.ziplly.app.shared.ValidationResult;

public class LoginWidget extends Composite {

	private static LoginWidgetUiBinder uiBinder = GWT
			.create(LoginWidgetUiBinder.class);

	interface LoginWidgetUiBinder extends UiBinder<Widget, LoginWidget> {
	}

	private static final String ACCOUNT_DOES_NOT_EXIST = "Account with this email doesn't exist";
	private static final String INVALID_ACCOUNT_CREDENTIALS = "Invalid account credentials.";
	private OAuthConfig authConfig = OAuthFactory
			.getAuthConfig(OAuthProvider.FACEBOOK.name());
	private CachingDispatcherAsync dispatcher;
	private SimpleEventBus eventBus;

	@Inject
	public LoginWidget(CachingDispatcherAsync dispatcher,
			SimpleEventBus eventBus) {
		this.dispatcher = dispatcher;
		this.eventBus = eventBus;
		initWidget(uiBinder.createAndBindUi(this));
		message.setVisible(false);
	}

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
	Button fbLoginBtn;

	@UiField
	Button loginBtn;

	@UiField
	Alert message;

	@UiHandler("fbLoginBtn")
	void fbLogin(ClickEvent event) {
		try {
			Window.Location.replace(authConfig.getAuthorizationUrl());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
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
		result = FieldVerifier.validatePassword(passwordInput);

		if (!result.isValid()) {
			passwordError.setText(result.getErrors().get(0).getErrorMessage());
			passwordCg.setType(ControlGroupType.ERROR);
			passwordError.setVisible(true);
			valid = false;
		}

		return valid;
	}

	void setMessage(String msg, AlertType type) {
		message.setType(type);
		message.setText(msg);
		message.setVisible(true);
	}

	void resetForm() {
		email.setText("");
		password.setText("");
	}

	@UiHandler("loginBtn")
	void login(ClickEvent event) {
		if (!validateInput()) {
			return;
		}

		String emailInput = FieldVerifier.getEscapedText(email.getText());
		String passwordInput = FieldVerifier.getEscapedText(password.getText());
		dispatcher.execute(new ValidateLoginAction(emailInput, passwordInput),
				new DispatcherCallbackAsync<ValidateLoginResult>() {
					@Override
					public void onSuccess(ValidateLoginResult result) {
						if (result != null) {
							eventBus.fireEvent(new LoginEvent(result
									.getAccount()));
							History.newItem("main");
						} else {
							LoginWidget.this.setMessage(
									INVALID_ACCOUNT_CREDENTIALS,
									AlertType.ERROR);
						}
						resetForm();
					}

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof NotFoundException) {
							LoginWidget.this.setMessage(ACCOUNT_DOES_NOT_EXIST,
									AlertType.ERROR);
						} else if (caught instanceof InvalidCredentialsException) {
							LoginWidget.this.setMessage(
									INVALID_ACCOUNT_CREDENTIALS,
									AlertType.ERROR);
						}
					}
				});
	}
}
