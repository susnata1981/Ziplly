package com.ziplly.app.client.view;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.client.oauth.OAuthConfig;
import com.ziplly.app.client.oauth.OAuthFactory;
import com.ziplly.app.client.oauth.OAuthProvider;
import com.ziplly.app.client.places.SignupPlace;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.client.widget.MyBundle;
import com.ziplly.app.model.TweetDTO;

public class MainView extends Composite implements View<Presenter> {
	private static MainViewUiBinder uiBinder = GWT
			.create(MainViewUiBinder.class);

	interface MainViewUiBinder extends UiBinder<Widget, MainView> {
	}

	public interface MainViewStyle extends CssResource {
		String individualProfilePerCategory();

		String categoryHeading();
	}

	private OAuthConfig authConfig = OAuthFactory
			.getAuthConfig(OAuthProvider.FACEBOOK.name());

	@UiField
	MyBundle resource;

	@UiField
	Button signupBtn;
	
	@UiField
	Button fbLoginButtonOnMainPage;

	@UiField
	HTMLPanel categoryDetailsSection;

	@UiField
	MainViewStyle mainViewStyle;

	Presenter presenter;

	public MainView() {
		initWidget(uiBinder.createAndBindUi(this));
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
	
	public void showLogin() {
		fbLoginButtonOnMainPage.setVisible(true);
	}

	@UiHandler("fbLoginButtonOnMainPage")
	void fbLogin(ClickEvent event) {
		try {
			Window.Location.replace(authConfig.getAuthorizationUrl());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Called after user logs in
	 *
	private void doLogin(AccountDTO account) {
		this.account = account;
		disableLoginBtns(true);
	}

	private String getLogoutUrl() throws UnsupportedEncodingException {
		if (account != null) {
			return OAuthConfigConstants.FB_LOGOUT_URL + "?" + "next="
					+ authConfig.getRedirectUri() + "&access_token="
					+ account.getAccessToken();
		}
		throw new IllegalStateException("Trying to get logout url for non-logged in user");
	}
	*/
	
	@UiHandler("signupBtn")
	public void signup(ClickEvent event) {
		presenter.goTo(new SignupPlace());
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void clear() {
	}
}
