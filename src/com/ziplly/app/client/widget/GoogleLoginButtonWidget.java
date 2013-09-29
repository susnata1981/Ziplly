package com.ziplly.app.client.widget;

import java.io.UnsupportedEncodingException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.oauth.OAuthConfig;

public class GoogleLoginButtonWidget extends Composite {

	private static GoogleLoginButtonWidgetUiBinder uiBinder = GWT
			.create(GoogleLoginButtonWidgetUiBinder.class);

	interface GoogleLoginButtonWidgetUiBinder extends
			UiBinder<Widget, GoogleLoginButtonWidget> {
	}

	private OAuthConfig authConfig;

	public GoogleLoginButtonWidget(OAuthConfig authConfig) {
		this.authConfig = authConfig;
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Button googleLoginButton;

	public GoogleLoginButtonWidget(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiFactory
	public MyBundle createTheBundle() {
		MyBundle.INSTANCE.style().ensureInjected();
		return MyBundle.INSTANCE;
	}
	
	@UiHandler("googleLoginButton")
	void onClick(ClickEvent e) {
		try {
			//RootPanel.get("popup").add(panel);
			Window.Location.replace(authConfig.getAuthorizationUrl());
		} catch (UnsupportedEncodingException e1) {
			Window.alert(e1.getMessage());
		}
	}
	
	@UiField
	MyBundle res;
}
