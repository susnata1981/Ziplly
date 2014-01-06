package com.ziplly.app.client.view;

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
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.oauth.OAuthConfig;
import com.ziplly.app.client.oauth.OAuthFactory;
import com.ziplly.app.client.oauth.OAuthProvider;
import com.ziplly.app.client.places.BusinessSignupPlace;
import com.ziplly.app.client.places.SignupPlace;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.client.view.HomeView.HomePresenter;
import com.ziplly.app.client.widget.CssStyleHelper;
import com.ziplly.app.client.widget.MyBundle;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class MainView extends Composite implements View<HomePresenter> {
	private static MainViewUiBinder uiBinder = GWT
			.create(MainViewUiBinder.class);
	interface MainViewUiBinder extends UiBinder<Widget, MainView> {
	}

	public interface MainViewStyle extends CssResource {
		String individualProfilePerCategory();

		String categoryHeading();
	}

	private OAuthConfig authConfig = OAuthFactory.getAuthConfig(OAuthProvider.FACEBOOK.name());

	@UiField
	MyBundle resource;

	@UiField
	Anchor residentSignupLink;
	
	@UiField
	Button fbLoginButtonOnMainPage;

	@UiField
	HTMLPanel residentSignupPanel;
	
	@UiField
	HTMLPanel descriptionPanel;

	@UiField
	MainViewStyle mainViewStyle;

	@UiField
	Button businessSignupBtn;
	
	HomePresenter presenter;

	public MainView() {
		initWidget(uiBinder.createAndBindUi(this));
		displayHelpFields(false);
		displayMessageField(false);
	}

	@UiFactory
	MyBundle myBundle() {
		MyBundle.INSTANCE.style().ensureInjected();
		return MyBundle.INSTANCE;
	}

	@UiFactory
	ZResources resources() {
		ZResources.IMPL.style().ensureInjected();
		return ZResources.IMPL;
	}

	@Override
	public void onLoad() {
		System.out.println("Setting background image");
		CssStyleHelper.setBackgroundImage(ZResources.IMPL.neighborhoodLargePic());
	}
	
	public void showLogin() {
		fbLoginButtonOnMainPage.setVisible(true);
	}

	@UiHandler("fbLoginButtonOnMainPage")
	void fbLogin(ClickEvent event) {
		try {
			System.out.println("MODULE BASE URL:"+GWT.getHostPageBaseURL());
			String url = GWT.getHostPageBaseURL();
			url = url.replace("127.0.0.1", "localhost") + "Ziplly.html";
			authConfig.setRedirectUri(url);
			Window.Location.replace(authConfig.getAuthorizationUrl());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@UiHandler("residentSignupLink")
	void signup(ClickEvent event) {
		presenter.goTo(new SignupPlace());
	}
	
	@UiHandler("businessSignupBtn")
	public void businessSignup(ClickEvent event) {
		presenter.goTo(new BusinessSignupPlace(""));
	}
	
	@Override
	public void clear() {
		resetLoginForm();
		resetMessage();
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
	Button loginBtn;

	@UiField
	Alert message;


	boolean validateInput() {
		String emailInput = email.getText();
		ValidationResult result = FieldVerifier.validateEmail(emailInput);
		boolean valid = true;

		if (!result.isValid()) {
			emailError.setText(result.getErrors().get(0).getErrorMessage());
			emailCg.setType(ControlGroupType.ERROR);
//			emailError.setVisible(true);
			valid = false;
		}

		String passwordInput = password.getText();
		result = FieldVerifier.validatePassword(passwordInput);

		if (!result.isValid()) {
			passwordError.setText(result.getErrors().get(0).getErrorMessage());
			passwordCg.setType(ControlGroupType.ERROR);
//			passwordError.setVisible(true);
			valid = false;
		}

		return valid;
	}

	@UiHandler("loginBtn")
	void login(ClickEvent event) {
		if (!validateInput()) {
			displayHelpFields(true);
			return;
		}
		String emailInput = FieldVerifier.getEscapedText(email.getText());
		String passwordInput = FieldVerifier.getEscapedText(password.getText());
		presenter.onLogin(emailInput, passwordInput);
	}

	public void displayMessage(String msg, AlertType type) {
		message.setText(msg);
		message.setType(type);
		displayMessageField(true);
	}

	public void resetLoginForm() {
		emailCg.setType(ControlGroupType.NONE);
		email.setText("");
		emailError.setText("");
		passwordCg.setType(ControlGroupType.NONE);
		password.setText("");
		passwordError.setText("");
		displayHelpFields(false);
	}

	public void resetMessage() {
		message.clear();
		displayMessageField(false);
	}

	private void displayMessageField(boolean b) {
		Display display = b == true ? Display.BLOCK : Display.NONE;
		message.getElement().getStyle().setDisplay(display);
	}

	private void displayHelpFields(boolean b) {
		Display display = b == true ? Display.BLOCK : Display.NONE;
		emailError.getElement().getStyle().setDisplay(display);
		passwordError.getElement().getStyle().setDisplay(display);
	}
	
	@Override
	public void setPresenter(HomePresenter presenter) {
		this.presenter = presenter;
	}

	public void setRedirectUri(String redirectUrl) {
		authConfig.setRedirectUri(redirectUrl);
	}
}
