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
import com.ziplly.app.client.view.SignupView;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountDetails;

public class MainController implements ValueChangeHandler<String> {
	private static final String BACKGROUND_IMG_URL = "url('neighborhood_large.jpg')";
	private HasWidgets container;
	private ZipllyServiceAsync service;
	private SimpleEventBus eventBus;
	private MainView mainView;
	private AccountView accountView;
	private NavView navView;
	private AccountDetails ad;
	private Logger logger = Logger.getLogger("MainController");
	protected AccountDTO account;
	public MainController(//final HasWidgets widget,
			SimpleEventBus eventBus) {
		this.container = RootPanel.get("main");
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
		service.doLogin(code, new AsyncCallback<AccountDTO>() {
			@Override
			public void onFailure(Throwable caught) {
				logger.log(Level.SEVERE, "Failed to handle auth:" + caught.getMessage());
			}

			@Override
			public void onSuccess(AccountDTO account) {
				if (account != null) {
					MainController.this.account = account;
//					dropLoginCookie(account);
					eventBus.fireEvent(new LoginEvent(account));
					MainController.this.logger.log(Level.INFO, "User: " + account.getDisplayName()
							+ "(" + account.getId() + ") logged in.");
				}
			}
		});
	}

	void dropLoginCookie(AccountDTO account) {
		CookieManager.dropLoginCookie(account);
	}

	/*
	 * Main entry point
	 */
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
				setBackgroundImage();
				display(container, mainView);
			} 
			else if (token.startsWith("account")) {
				clearBackgroundImage();
				MatchResult result = accountPath.exec(token);
				display(container, accountView);
			}
			else if (token.startsWith("signup")) {
				setBackgroundImage();
				SignupView signupView = new SignupView(eventBus);
				display(container, signupView);
			}
		}
	}

	void setBackgroundImage() {
		RootPanel.get("wrapper").getElement().getStyle().setBackgroundImage(BACKGROUND_IMG_URL);
		RootPanel.get("wrapper").getElement().getStyle().setProperty("backgroundSize", "cover");
	}
	
	void clearBackgroundImage() {
		RootPanel.get("wrapper").getElement().getStyle().setBackgroundImage("none");
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
