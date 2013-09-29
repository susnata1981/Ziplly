package com.ziplly.app.client.widget;

import java.io.UnsupportedEncodingException;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.ZipllyServiceAsync;
import com.ziplly.app.client.cookie.CookieManager;
import com.ziplly.app.client.oauth.OAuthConfig;
import com.ziplly.app.client.oauth.OAuthConfigConstants;
import com.ziplly.app.client.oauth.OAuthFactory;
import com.ziplly.app.client.oauth.OAuthProvider;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.view.event.LogoutEvent;
import com.ziplly.app.client.view.handler.LoginEventHandler;
import com.ziplly.app.model.AccountDetails;

public class LogoutWidget extends Composite {

	private static LogoutWidgetUiBinder uiBinder = GWT
			.create(LogoutWidgetUiBinder.class);

	interface LogoutWidgetUiBinder extends UiBinder<Widget, LogoutWidget> {
	}

	private SimpleEventBus eventBus;
	private ZipllyServiceAsync service;
	private OAuthConfig authConfig = OAuthFactory
			.getAuthConfig(OAuthProvider.FACEBOOK.name());
	private AccountDetails ad;

	@UiField
	Button logoutBtn;

	public LogoutWidget(ZipllyServiceAsync service, SimpleEventBus eventBus) {
		this.service = service;
		this.eventBus = eventBus;
		initWidget(uiBinder.createAndBindUi(this));
		setup();
	}

	private void setup() {
		eventBus.addHandler(LoginEvent.TYPE, new LoginHandler(this));
	}

	private String getLogoutUrl() throws UnsupportedEncodingException {
		if (ad.account != null) {
			return OAuthConfigConstants.FB_LOGOUT_URL + "?" + "next="
					+ authConfig.getRedirectUri() + "&access_token="
					+ ad.account.getAccessToken();
		}
		throw new IllegalStateException("Trying to get logout url for non-logged account");
	}

	@UiHandler("logoutBtn")
	void logout(ClickEvent event) {
		if (ad.account == null) {
			return;
		}

		String logoutUrl = null;
		try {
			logoutUrl = getLogoutUrl();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		CookieManager.removeLoginCookie();
		service.logoutAccount(new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				System.out.println("Failed to logout user...");
			}

			@Override
			public void onSuccess(Void result) {
				System.out.println("User successfully logged out...");
				eventBus.fireEvent(new LogoutEvent());
			}
		});
		Window.Location.replace(logoutUrl);
	}

	private static class LoginHandler implements LoginEventHandler {
		private LogoutWidget lw;

		public LoginHandler(final LogoutWidget lw) {
			this.lw = lw;
		}

		@Override
		public void onEvent(LoginEvent event) {
			lw.ad = event.getAccountDetails();
		}
	}
}
