package com.ziplly.app.client.widget;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.ziplly.app.client.activities.AccountActivityPresenter;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.client.view.View;

public class LogoutWidget extends Composite implements View {

	private static LogoutWidgetUiBinder uiBinder = GWT
			.create(LogoutWidgetUiBinder.class);

	interface LogoutWidgetUiBinder extends UiBinder<Widget, LogoutWidget> {
	}

//	private OAuthConfig authConfig = OAuthFactory.getAuthConfig(OAuthProvider.FACEBOOK.name());

	@UiField
	Button logoutBtn;
	private AccountActivityPresenter presenter;

	@Inject
	public LogoutWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

//	String getLogoutUrl() throws UnsupportedEncodingException {
//		if (account != null) {
//			return OAuthConfigConstants.FB_LOGOUT_URL + "?" + "next="
//					+ authConfig.getRedirectUri() + "&access_token="
//					+ account.getAccessToken();
//		}
//		throw new IllegalStateException("Trying to get logout url for non-logged account");
//	}

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
		
//		if (account == null) {
//			logger.log(Level.WARNING, "Trying to log out a unlogged user");
//			return;
//		}
//		
//		dispatcher.execute(new LogoutAction(account.getUid()), new DispatcherCallbackAsync<LogoutResult>() {
//			@Override
//			public void onSuccess(LogoutResult result) {
//				eventBus.fireEvent(new LogoutEvent());
//				History.newItem("main");
//			}
//		});
		
		if (presenter != null) {
			presenter.logout();
		}
	}

	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public void setPresenter(Presenter presenter) {
		if (presenter instanceof AccountActivityPresenter) {
			this.presenter = (AccountActivityPresenter)presenter;
		}
	}

	@Override
	public void clear() {
	}
}
