package com.ziplly.app.client.activities;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.exceptions.InvalidCredentialsException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.places.AccountPlace;
import com.ziplly.app.client.view.LoginAwareView;
import com.ziplly.app.client.widget.LoginWidget;
import com.ziplly.app.shared.ValidateLoginAction;
import com.ziplly.app.shared.ValidateLoginResult;

public abstract class AbstractLoginAwareActivity<T extends LoginAwareView> extends AbstractActivity {
	protected T view;
	
	public AbstractLoginAwareActivity(CachingDispatcherAsync dispatcher,
			EventBus eventBus, PlaceController placeController,
			T view) {
		super(dispatcher, eventBus, placeController);
		this.view = view;
	}

	public void validateLogin(String email, String password) {
		dispatcher.execute(new ValidateLoginAction(email, password),
		new DispatcherCallbackAsync<ValidateLoginResult>() {
			@Override
			public void onSuccess(ValidateLoginResult result) {
				if (result != null) {
//					eventBus.fireEvent(new LoginEvent(result
//							.getAccount()));
					goTo(new AccountPlace());
				} else {
					view.displayLoginErrorMessage(LoginWidget.INVALID_ACCOUNT_CREDENTIALS, AlertType.ERROR);
				}
				view.resetLoginForm();
			}

			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof NotFoundException) {
					view.displayLoginErrorMessage(LoginWidget.ACCOUNT_DOES_NOT_EXIST, AlertType.ERROR);
				} else if (caught instanceof InvalidCredentialsException) {
					view.displayLoginErrorMessage(LoginWidget.INVALID_ACCOUNT_CREDENTIALS, AlertType.ERROR);
				}
				view.resetLoginForm();
			}
		});
	}
}
