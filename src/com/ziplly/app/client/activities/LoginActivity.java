package com.ziplly.app.client.activities;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.exceptions.AccountNotActiveException;
import com.ziplly.app.client.exceptions.InvalidCredentialsException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.view.ILoginAccountView;
import com.ziplly.app.client.view.LoginAccountView;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.widget.LoginWidget;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.shared.ValidateLoginAction;
import com.ziplly.app.shared.ValidateLoginResult;

public class LoginActivity extends AbstractActivity implements LoginPresenter {
	private ILoginAccountView<LoginPresenter> view;
	@SuppressWarnings("unused")
  private LoginPlace place;
	private AcceptsOneWidget panel;
	private AsyncProvider<LoginAccountView> viewProvider;

	@Inject
	public LoginActivity(CachingDispatcherAsync dispatcher,
	    EventBus eventBus,
	    LoginPlace place,
	    PlaceController placeController,
	    ApplicationContext ctx,
	    AsyncProvider<LoginAccountView> viewProvider) {
		super(dispatcher, eventBus, placeController, ctx);
		this.place = place;
		this.viewProvider = viewProvider;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		checkAccountLogin();
	}

	@Override
	protected void doStart() {
		placeController.goTo(new HomePlace());
	}
	
	@Override
	protected void doStartOnUserNotLoggedIn() {
		viewProvider.get(new DefaultViewLoaderAsyncCallback<LoginAccountView>() {

			@Override
			public void onSuccess(LoginAccountView result) {
				LoginActivity.this.view = result;
				bind();
				go(LoginActivity.this.panel);
			}
		});
	}

	@Override
	public void onStop() {
		view.clear();
		StyleHelper.clearBackground();
	}

	@Override
	public void go(AcceptsOneWidget container) {
		container.setWidget(view);
	}

	@Override
	public void bind() {
		view.setPresenter(this);
	}

	@Override
	public void onLogin(String email, String password) {
		dispatcher.execute(
		    new ValidateLoginAction(email, password),
		    new DispatcherCallbackAsync<ValidateLoginResult>() {
			    @Override
			    public void onSuccess(ValidateLoginResult result) {
				    if (result != null && result.getAccount() != null) {
					    ctx.setAccount(result.getAccount());
					    eventBus.fireEvent(new LoginEvent(result.getAccount()));
					    if (ctx.getLastPlace() != null) {
					    	goTo(ctx.getLastPlace());
					    	return;
					    }
					    goTo(new HomePlace());
				    }
			    }

			    @Override
			    public void onFailure(Throwable caught) {
				    if (caught instanceof NotFoundException) {
					    view.displayMessage(LoginWidget.ACCOUNT_DOES_NOT_EXIST, AlertType.ERROR);
				    } else if (caught instanceof AccountNotActiveException) {
				    	view.displayMessage(LoginWidget.ACCOUNT_NOT_ACTIVE, AlertType.ERROR);
				    } else if (caught instanceof InvalidCredentialsException) {
					    view.displayMessage(LoginWidget.INVALID_ACCOUNT_CREDENTIALS, AlertType.ERROR);
				    } else {
					    view.displayMessage(StringConstants.INTERNAL_ERROR, AlertType.ERROR);
				    }
				    view.resetLoginForm();
			    }
		    });
	}
}
