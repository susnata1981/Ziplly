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
import com.ziplly.app.client.view.HomeView;
import com.ziplly.app.client.view.MainView;
import com.ziplly.app.client.view.NavView;
import com.ziplly.app.client.view.SignupView;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.model.AccountDTO;

public class MainController implements ValueChangeHandler<String> {
	private static final String BACKGROUND_IMG_URL = "url('neighborhood_large.jpg')";
	private HasWidgets container;
	private ZipllyServiceAsync service;
	private SimpleEventBus eventBus;
	private MainView mainView;
	private AccountView accountView;
	private NavView navView;
	private Logger logger = Logger.getLogger("MainController");
	protected AccountDTO account;
	private HomeView homeView;

	public MainController(SimpleEventBus eventBus) {
		this.container = RootPanel.get("main");
		this.service = GWT.create(ZipllyService.class);
		this.eventBus = eventBus;
		this.accountView = new AccountView(eventBus);
		this.navView = new NavView(eventBus);
		this.mainView = new MainView(eventBus);
		this.homeView = new HomeView(eventBus);
		RootPanel.get("nav").add(navView);
		init();
	}

	boolean isUserLoggedIn() {
		return (this.account != null);
	}

	void init() {
		if (isUserLoggedIn()) {
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
		logger.log(Level.INFO, "Found logged in user: " + accountId);
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
				logger.log(Level.SEVERE, "Error logging in user:" + e.getMessage());
			}
		}
	}

	private void handleAuth(String code) {
		service.getFacebookUserDetails(code, new AsyncCallback<AccountDTO>() {
			@Override
			public void onFailure(Throwable caught) {
				logger.log(Level.SEVERE, "Failed to handle auth:" + caught.getMessage());
			}

			@Override
			public void onSuccess(AccountDTO account) {
				if (account != null) {
					MainController.this.account = account;
					// dropLoginCookie(account);

					// MainController.this.logger.log(Level.INFO, "User: " +
					// account.getDisplayName()
					// + "(" + account.getId() + ") logged in.");
					// History.newItem("home");
					// redirect to signup page
					System.out.println("Received account:"+account+" ("+account.getId()+")");
					System.out.println("Url="+account.getUrl());
					if (account.getId() != null) {
						// logged in user
						eventBus.fireEvent(new LoginEvent(account));
					} else {
						setBackgroundImage();
						SignupView signupView = new SignupView(eventBus);
						signupView.displayAccount(account);
						display(container, signupView);
					}
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
			History.newItem("main");
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
			if (token.equals("main")) {
				// TODO: Hack
				if (isUserLoggedIn()) {
					display(container, homeView);
					return;
				}
				setBackgroundImage();
				display(container, mainView);
			} else if (token.startsWith("signup")) {
				setBackgroundImage();
				SignupView signupView = new SignupView(eventBus);
				display(container, signupView);
			} else if (token.equals("home")) {
				clearBackgroundImage();
				display(container, homeView);
			} else if (token.startsWith("account")) {
				clearBackgroundImage();
				MatchResult result = accountPath.exec(token);
				display(container, accountView);
			}
		}
	}

	void setBackgroundImage() {
		RootPanel.get("wrapper").getElement().getStyle()
				.setBackgroundImage(BACKGROUND_IMG_URL);
		RootPanel.get("wrapper").getElement().getStyle()
				.setProperty("backgroundSize", "cover");
	}

	void clearBackgroundImage() {
		RootPanel.get("wrapper").getElement().getStyle()
				.setBackgroundImage("none");
	}

	// private static class AccountLoginByCookieHandler implements
	// AsyncCallback<AccountDetails> {
	// private MainController controller;
	//
	// public AccountLoginByCookieHandler(MainController controller) {
	// this.controller = controller;
	// }
	//
	// @Override
	// public void onFailure(Throwable caught) {
	// // TODO log it
	// }
	//
	// @Override
	// public void onSuccess(Account account) {
	// if (account == null) {
	// return;
	// }
	// controller.account = account;
	// controller.eventBus.fireEvent(new LoginEvent(account));
	// }
	// }

	public void display(HasWidgets widget, Composite c) {
		widget.clear();
		widget.add(c);
	}
}
