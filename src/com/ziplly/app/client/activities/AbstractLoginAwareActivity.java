package com.ziplly.app.client.activities;


public abstract class AbstractLoginAwareActivity {
/*	protected T view;
	
	public AbstractActivity(CachingDispatcherAsync dispatcher,
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
					goTo(new LoginPlace());
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
	}*/
}
