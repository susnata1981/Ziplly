package com.ziplly.app.client.activities;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.exceptions.InvalidCredentialsException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.view.ISignupView;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.widget.LoginWidget;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.shared.GetImageUploadUrlAction;
import com.ziplly.app.shared.GetImageUploadUrlResult;
import com.ziplly.app.shared.RegisterAccountAction;
import com.ziplly.app.shared.RegisterAccountResult;
import com.ziplly.app.shared.ValidateLoginAction;
import com.ziplly.app.shared.ValidateLoginResult;

public abstract class AbstractSignupActivity extends AbstractActivity implements SignupActivityPresenter {
	ISignupView<SignupActivityPresenter> view;
	
	public AbstractSignupActivity(CachingDispatcherAsync dispatcher,
			EventBus eventBus, PlaceController placeController,
			ApplicationContext ctx,
			ISignupView<SignupActivityPresenter> view) {
		super(dispatcher, eventBus, placeController, ctx);
		this.view = view;
	}

	@Override
	public void onLogin(String email, String password) {
		dispatcher.execute(new ValidateLoginAction(email, password),
				new DispatcherCallbackAsync<ValidateLoginResult>() {
					@Override
					public void onSuccess(ValidateLoginResult result) {
						if (result != null && result.getAccount() != null) {
							ctx.setAccount(result.getAccount());
							forward(result.getAccount());
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof NotFoundException) {
							view.displayMessage(
									LoginWidget.ACCOUNT_DOES_NOT_EXIST,
									AlertType.ERROR);
						} else if (caught instanceof InvalidCredentialsException) {
							view.displayMessage(
									LoginWidget.INVALID_ACCOUNT_CREDENTIALS,
									AlertType.ERROR);
						}
						view.resetLoginForm();
					}
		});
	}

	@Override
	public void register(AccountDTO account) {
		dispatcher.execute(new RegisterAccountAction(account),
				new DispatcherCallbackAsync<RegisterAccountResult>() {
					@Override
					public void onSuccess(RegisterAccountResult result) {
						System.out.println("Account " + result.getAccount()
								+ " registered.");
						eventBus.fireEvent(new LoginEvent(result.getAccount()));
						forward(result.getAccount());
					}
				});
	}

	@Override
	public void setImageUploadUrl() {
		dispatcher.execute(new GetImageUploadUrlAction(),
				new DispatcherCallbackAsync<GetImageUploadUrlResult>() {
					@Override
					public void onSuccess(GetImageUploadUrlResult result) {
						// TODO hack for making it work in local environment
						String url = result.getImageUrl().replace(
								"susnatas-MacBook-Pro.local:8888",
								"127.0.0.1:8888");
						System.out.println("Setting upload image form action to:"+url);
						view.setImageUploadUrl(url);
					}
				});
	}

	// TODO handle image deletion on multiple file uploads
	@Override
	public void setUploadImageHandler() {
		view.addUploadFormHandler(new FormPanel.SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				String imageUrl = event.getResults();
				System.out.println("Received uploaded image url:"+imageUrl);
				view.displayProfileImagePreview(imageUrl);
				view.reset();
				setImageUploadUrl();
//				view.setImageUploadUrl(imageUrl);
//				profileImagePreview.setUrl(imageUrl);
//				profileImagePreview.setVisible(true);
//				SignupView.this.profileImageUrl = imageUrl;
			}
		});
	}

}
