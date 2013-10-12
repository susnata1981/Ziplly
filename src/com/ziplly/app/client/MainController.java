package com.ziplly.app.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.inject.Inject;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.view.AccountView;
import com.ziplly.app.client.view.HomeView;
import com.ziplly.app.client.view.MainView;
import com.ziplly.app.client.view.NavView;
import com.ziplly.app.client.view.SignupView;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.view.event.LogoutEvent;
import com.ziplly.app.client.view.handler.LoginEventHandler;
import com.ziplly.app.client.view.handler.LogoutEventHandler;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.shared.GetFacebookDetailsAction;
import com.ziplly.app.shared.GetFacebookDetailsResult;
import com.ziplly.app.shared.GetLoggedInUserAction;
import com.ziplly.app.shared.GetLoggedInUserResult;

public class MainController implements ValueChangeHandler<String> {
	private static final String BACKGROUND_IMG_URL = "url('neighborhood_large.jpg')";
	private HasWidgets container;
	private SimpleEventBus eventBus;
	@Inject
	private MainView mainView;
	@Inject
	private AccountView accountView;
	@Inject
	private HomeView homeView;
	@Inject
	SignupView signupView;

	private NavView navView;
	private Logger logger = Logger.getLogger("MainController");
	protected AccountDTO account;
	CachingDispatcherAsync dispatcher;

	@Inject
	public MainController(SimpleEventBus eventBus,
			CachingDispatcherAsync dispatcher) {
//		ZGinInjector injector = GWT.create(ZGinInjector.class);
		this.container = RootPanel.get("main");
		this.dispatcher = dispatcher;
		this.eventBus = eventBus;
//		this.accountView = new AccountView(dispatcher, eventBus);
		this.navView = new NavView(eventBus);
//		this.mainView = new MainView(dispatcher, eventBus);
//		this.homeView = new HomeView(dispatcher, eventBus);
//		this.signupView = injector.getSignupView();
		RootPanel.get("nav").add(navView);
		init();
	}

	boolean isUserLoggedIn() {
		return (this.account != null);
	}

	void init() {
		History.addValueChangeHandler(this);
		eventBus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {
			@Override
			public void onEvent(LoginEvent event) {
				MainController.this.account = event.getAccount();
				History.newItem("account");
			}
		});
		eventBus.addHandler(LogoutEvent.TYPE, new LogoutEventHandler() {
			@Override
			public void onEvent(LogoutEvent event) {
				doLogout();
				History.newItem("main");
			}
		});
	}
	
	private void doLogout() {
		this.account = null;
	}

	void getLoggedInUser() {
		if (account == null) {
			dispatcher.execute(new GetLoggedInUserAction(),
					new DispatcherCallbackAsync<GetLoggedInUserResult>() {
						@Override
						public void onSuccess(GetLoggedInUserResult result) {
							if (result != null && result.getAccount() != null) {
								// user logged in
								MainController.this.account = result.getAccount();
							}
						}
					});
		}
	}

	private void handleOAuthRedirect() {
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
		dispatcher.execute(new GetFacebookDetailsAction(code),
				new DispatcherCallbackAsync<GetFacebookDetailsResult>() {
					@Override
					public void onSuccess(GetFacebookDetailsResult result) {
						AccountDTO account = result.getAccount();
						if (account != null) {
							if (account.getAccountId() != null) {
								// logged in user
								eventBus.fireEvent(new LoginEvent(account));
							} else {
								setBackgroundImage();
								signupView.displayAccount(account);
								display(container, signupView);
							}
						}
					}
				});
	}

	/*
	 * Main entry point
	 */
	public void go() {
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
		handleOAuthRedirect();

		if (token != null) {
			if (token.equals("main")) {
				if (account != null) {
					// user logged in
					System.out.println("User:"+account+" logged in");
					display(container, homeView);
				} else {
					setBackgroundImage();
					display(container, mainView);
				}
			} else if (token.startsWith("signup")) {
				setBackgroundImage();
				// SignupView signupView = new SignupView(eventBus);
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

	public void display(HasWidgets widget, Composite c) {
		widget.clear();
		widget.add(c);
	}
}
