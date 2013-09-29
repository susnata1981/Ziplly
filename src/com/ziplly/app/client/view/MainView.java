package com.ziplly.app.client.view;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.ziplly.app.client.cookie.CookieManager;
import com.ziplly.app.client.oauth.OAuthConfig;
import com.ziplly.app.client.oauth.OAuthConfigConstants;
import com.ziplly.app.client.oauth.OAuthFactory;
import com.ziplly.app.client.oauth.OAuthProvider;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.view.event.LogoutEvent;
import com.ziplly.app.client.view.handler.LoginEventHandler;
import com.ziplly.app.client.view.handler.LogoutEventHandler;
import com.ziplly.app.client.widget.MyBundle;
import com.ziplly.app.client.widget.cell.AccountDetailsMiniCell;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDetails;
import com.ziplly.app.model.CategoryDetails;

public class MainView extends AbstractView {
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
	Button logoutBtn;

	@UiField
	Button searchBtn;

	@UiField
	TextBox searchField;

//	@UiField(provided = true)
//	LoginWidget loginWidget;

	@UiField
	Button fbLoginButtonOnMainPage;

	@UiField
	MainViewStyle mainViewStyle;

	private AccountDetails ad;

	public MainView(SimpleEventBus eventBus) {
		super(eventBus);
	}

	@Override
	protected void initWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	protected void postInitWidget() {
		System.out.println("MainView onLoad...");
		if (ad != null && ad.account != null) {
			disableLoginBtns(true);
		} else {
			disableLoginBtns(false);
		}
		fetchCategoryDetailsData();
	}

	@Override
	protected void setupUiElements() {
//		loginWidget = WidgetFactory.getLoginWidget(service, eventBus);
	}

	@Override
	protected void setupHandlers() {
		eventBus.addHandler(LoginEvent.TYPE, new LoginHandler(this));
		eventBus.addHandler(LogoutEvent.TYPE, new LogoutHandler(this));
	}

	@UiFactory
	MyBundle myBundle() {
		MyBundle.INSTANCE.style().ensureInjected();
		return MyBundle.INSTANCE;
	}

	protected void fetchCategoryDetailsData() {
	}

	/*
	 * Disables login button
	 */
	private void disableLoginBtns(Boolean disable) {
//		if (disable) {
//			loginWidget.hide();
//		} else {
//			loginWidget.show();
//		}
		fbLoginButtonOnMainPage.setVisible(!disable);
		logoutBtn.setVisible(disable);
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
	 */
	private void doLogin(AccountDetails ad) {
		this.ad = ad;
		disableLoginBtns(true);
	}

	private String getLogoutUrl() throws UnsupportedEncodingException {
		if (ad.account != null) {
			return OAuthConfigConstants.FB_LOGOUT_URL + "?" + "next="
					+ authConfig.getRedirectUri() + "&access_token="
					+ ad.account.getAccessToken();
		}
		throw new IllegalStateException("Trying to get logout url for non-logged in user");
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
		eventBus.fireEvent(new LogoutEvent());
		getService().logoutAccount(new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				logger.log(Level.SEVERE,
						"Failed to logout user: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				logoutUser();
			}
		});
		Window.Location.replace(logoutUrl);
	}

	@UiHandler("searchBtn")
	void searchByCategory(ClickEvent event) {
		String searchCategory = SafeHtmlUtils.fromString(searchField.getText())
				.asString().toLowerCase();
		History.newItem("category/" + searchCategory);
	}

	private static class LoginHandler implements LoginEventHandler {
		private MainView mainView;

		public LoginHandler(final MainView m) {
			this.mainView = m;
		}

		@Override
		public void onEvent(LoginEvent event) {
			mainView.doLogin(event.getAccountDetails());
			History.newItem("account");
		}
	}

	private static class LogoutHandler implements LogoutEventHandler {
		private MainView mainView;

		public LogoutHandler(final MainView m) {
			this.mainView = m;
		}

		@Override
		public void onEvent(LogoutEvent event) {
			mainView.logoutUser();
		}
	}

	public void logoutUser() {
		this.ad = null;
	}

	@UiField
	HTMLPanel categoryDetailsSection;

	/*
	 * Builds the list of category->accounts widgets
	 */
	public void displayCategoryDetails(List<CategoryDetails> input) {
		categoryDetailsSection.clear();
		for (CategoryDetails cd : input) {
			CellList<AccountDetails> accountList = new CellList<AccountDetails>(
					new AccountDetailsMiniCell());
			ListDataProvider<AccountDetails> dataProvider = new ListDataProvider<AccountDetails>();
			dataProvider.addDataDisplay(accountList);
			List<AccountDetails> accountListData = new ArrayList<AccountDetails>();
			VerticalPanel vp = new VerticalPanel();
			vp.setStyleName(mainViewStyle.individualProfilePerCategory());
			Heading heading = new Heading(4);
			heading.setStyleName(mainViewStyle.categoryHeading());
			heading.setText(cd.category.getCategoryType().toLowerCase());
			vp.add(heading);
			for (Account a : cd.accounts) {
				AccountDetails ad = new AccountDetails();
				ad.categories.add(cd.category);
				accountListData.add(ad);
				ad.account = a;
			}
			dataProvider.setList(accountListData);
			vp.add(accountList);
			categoryDetailsSection.add(vp);
		}
	}
}
