package com.ziplly.app.client.widget;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.oauth.OAuthConfig;
import com.ziplly.app.client.oauth.OAuthConfigConstants;
import com.ziplly.app.client.oauth.OAuthFactory;
import com.ziplly.app.client.oauth.OAuthProvider;
import com.ziplly.app.client.view.AbstractAccountView;
import com.ziplly.app.client.view.event.LogoutEvent;
import com.ziplly.app.shared.LogoutAction;
import com.ziplly.app.shared.LogoutResult;

public class LogoutWidget extends AbstractAccountView {

	private static LogoutWidgetUiBinder uiBinder = GWT
			.create(LogoutWidgetUiBinder.class);

	interface LogoutWidgetUiBinder extends UiBinder<Widget, LogoutWidget> {
	}

	private OAuthConfig authConfig = OAuthFactory
			.getAuthConfig(OAuthProvider.FACEBOOK.name());

	@UiField
	Button logoutBtn;

	@Inject
	public LogoutWidget(CachingDispatcherAsync dispatcher, SimpleEventBus eventBus) {
		super(dispatcher, eventBus);
	}

	@Override
	protected void internalOnUserLogin() {
	}

	@Override
	protected void initWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	protected void postInitWidget() {
	}

	@Override
	protected void setupUiElements() {
	}
	
	String getLogoutUrl() throws UnsupportedEncodingException {
		if (account != null) {
			return OAuthConfigConstants.FB_LOGOUT_URL + "?" + "next="
					+ authConfig.getRedirectUri() + "&access_token="
					+ account.getAccessToken();
		}
		throw new IllegalStateException("Trying to get logout url for non-logged account");
	}

	@UiHandler("logoutBtn")
	void logout(ClickEvent event) {
//		if (account == null) {
//			return;
//		}
//
//		String logoutUrl = null;
//		try {
//			logoutUrl = getLogoutUrl();
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		CookieManager.removeLoginCookie();
//		service.logoutAccount(new AsyncCallback<Void>() {
//			@Override
//			public void onFailure(Throwable caught) {
//				System.out.println("Failed to logout user...");
//			}
//
//			@Override
//			public void onSuccess(Void result) {
//				System.out.println("User successfully logged out...");
//				eventBus.fireEvent(new LogoutEvent());
//			}
//		});
//		Window.Location.replace(logoutUrl);
		
		if (account == null) {
			logger.log(Level.WARNING, "Trying to log out a unlogged user");
			return;
		}
		
		dispatcher.execute(new LogoutAction(account.getUid()), new DispatcherCallbackAsync<LogoutResult>() {
			@Override
			public void onSuccess(LogoutResult result) {
				eventBus.fireEvent(new LogoutEvent());
				History.newItem("main");
			}
		});
	}
}
