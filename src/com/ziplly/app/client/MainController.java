package com.ziplly.app.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootPanel;
import com.ziplly.app.client.cookie.CookieManager;
import com.ziplly.app.client.view.AccountView;
import com.ziplly.app.client.view.MainView;
import com.ziplly.app.client.view.NavView;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDetails;

public class MainController implements ValueChangeHandler<String> {
	private HasWidgets container;
	private ZipllyServiceAsync service;
	private SimpleEventBus eventBus;
	private MainView mainView;
	private AccountView accountView;
	private NavView navView;
	private AccountDetails ad;
	private Logger logger = Logger.getLogger("MainController");

	public MainController(final HasWidgets widget,
			SimpleEventBus eventBus) {
		this.container = widget;
		this.service = GWT.create(ZipllyService.class);
		this.eventBus = eventBus;
		this.accountView = new AccountView(eventBus);
		this.navView = new NavView(eventBus);
		this.mainView = new MainView(eventBus);
		RootPanel.get("nav").add(navView);
		setup();
	}

	void setup() {
		if (ad == null || ad.account == null) {
			checkLoginStatus();
		}
		History.addValueChangeHandler(this);
	}

	private void checkLoginStatus() {
		checkUserInfoInCookie();
	}

	private void checkUserInfoInCookie() {
		String accountId = CookieManager.getLoginCookie();
		if (accountId == null) {
			return;
		}
		logger.log(Level.INFO, "Found logged in user: "+accountId);
//		service.loginAccountById(Long.parseLong(accountId),
//				new LoginHandler<HandleLogin>(new HandleLogin() {
//					@Override
//					public void onLogin(AccountDetails ad) {
//						MainController.this.ad = ad;
//						eventBus.fireEvent(new LoginEvent(ad));
//					}
//				}));
	}

	private void handleOAuthRedirect() {
		if (CookieManager.isUserLoggedIn()) {
			History.newItem("home");
		}

		String code = Window.Location.getParameter("code");
		if (code != null) {
			try {
				handleAuth(code);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		History.newItem("home");
	}

	private void handleAuth(String code) {
		service.getAccessToken(code, new AsyncCallback<AccountDetails>() {
			@Override
			public void onFailure(Throwable caught) {
				logger.log(Level.SEVERE, "Failed to handle auth:" + caught.getMessage());
			}

			@Override
			public void onSuccess(AccountDetails ad) {
				if (ad.account != null) {
					MainController.this.ad = ad;
					dropLoginCookie(ad.account);
					eventBus.fireEvent(new LoginEvent(ad));
					MainController.this.logger.log(Level.INFO, "User: " + ad.account.getDisplayName()
							+ "(" + ad.account.getId() + ") logged in.");
				}
			}
		});
	}

	void dropLoginCookie(Account account) {
		CookieManager.dropLoginCookie(account);
	}

	public void go() {
		handleOAuthRedirect();
		if ("".equals(History.getToken())) {
			History.newItem("home");
		} else {
			History.fireCurrentHistoryState();
		}
	}
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		String token = event.getValue().trim();
		System.out.println("Token = " + token);
		RegExp accountPath = RegExp.compile("account/(\\S+)");
		
		if (token != null) {
			if (token.equals("home")) {
				display(container, mainView);
			} 
			else if (token.startsWith("account")) {
				MatchResult result = accountPath.exec(token);
				display(container, accountView);
//				navView.setActive(NavLinkEnum.ACCOUNT);
			} 
		}
	}

//	private static class AccountLoginByCookieHandler implements AsyncCallback<AccountDetails> {
//		private MainController controller;
//
//		public AccountLoginByCookieHandler(MainController controller) {
//			this.controller = controller;
//		}
//
//		@Override
//		public void onFailure(Throwable caught) {
//			// TODO log it
//		}
//
//		@Override
//		public void onSuccess(Account account) {
//			if (account == null) {
//				return;
//			}
//			controller.account = account;
//			controller.eventBus.fireEvent(new LoginEvent(account));
//		}
//	}

	public void display(HasWidgets widget, Composite c) {
		widget.clear();
		widget.add(c);
	}
}
