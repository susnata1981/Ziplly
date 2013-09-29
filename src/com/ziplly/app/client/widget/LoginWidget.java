package com.ziplly.app.client.widget;

import java.io.UnsupportedEncodingException;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.ZipllyServiceAsync;
import com.ziplly.app.client.oauth.OAuthConfig;
import com.ziplly.app.client.oauth.OAuthFactory;
import com.ziplly.app.client.oauth.OAuthProvider;

public class LoginWidget extends Composite {

	private static LoginWidgetUiBinder uiBinder = GWT
			.create(LoginWidgetUiBinder.class);

	interface LoginWidgetUiBinder extends UiBinder<Widget, LoginWidget> {
	}

	private OAuthConfig authConfig = OAuthFactory.getAuthConfig(OAuthProvider.FACEBOOK.name());
	
	public LoginWidget(ZipllyServiceAsync service, SimpleEventBus eventBus) {
		initWidget(uiBinder.createAndBindUi(this));
		hide();
	}
	
	@UiField
	Modal loginModalForm;
	
	@UiField
	Button fbLoginBtn;
	
	@UiField
	Button browseBtn;
	
	public void hide() {
		loginModalForm.hide();
	}
	
	public void show() {
		loginModalForm.show();
	}
	
	@UiHandler("browseBtn")
	public void close(ClickEvent event) {
		hide();
	}
	
	@UiHandler("fbLoginBtn")
	void fbLogin(ClickEvent event) {
		try {
			Window.Location.replace(authConfig.getAuthorizationUrl());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
