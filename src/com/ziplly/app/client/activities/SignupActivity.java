package com.ziplly.app.client.activities;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.places.SignupPlace;
import com.ziplly.app.client.view.SignupView;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;

public class SignupActivity extends AbstractSignupActivity {
	private SignupPlace place;

	@Inject
	public SignupActivity(CachingDispatcherAsync dispatcher, EventBus eventBus,
			PlaceController placeController, SignupPlace place, ApplicationContext ctx, SignupView view) {
		super(dispatcher, eventBus, placeController, ctx, view);
		this.place = place;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		bind();
		view.hideProfileImagePreview();
		view.reset();
		setImageUploadUrl();
		setUploadImageHandler();
		
		if (place.getAccount() != null) {
			AccountDTO a = place.getAccount();
			if (a instanceof PersonalAccountDTO ) {
				view.displayAccount((PersonalAccountDTO)a);
			}
		}
		panel.setWidget(view);
	}

	@Override
	public void fetchData() {
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
	public void onFacebookLogin() {
	}

	@Override
	public void onStop() {
		view.clear();
	}

//	@Override
//	public void register(AccountDTO account) {
//		dispatcher.execute(new RegisterAccountAction(account),
//				new DispatcherCallbackAsync<RegisterAccountResult>() {
//					@Override
//					public void onSuccess(RegisterAccountResult result) {
//						System.out.println("Account " + result.getAccount()
//								+ " registered.");
//						eventBus.fireEvent(new LoginEvent(result.getAccount()));
//						forward(result.getAccount());
//					}
//				});
//	}
//
//	@Override
//	public void onLogin(String email, String password) {
//		dispatcher.execute(new ValidateLoginAction(email, password),
//				new DispatcherCallbackAsync<ValidateLoginResult>() {
//					@Override
//					public void onSuccess(ValidateLoginResult result) {
//						if (result != null && result.getAccount() != null) {
//							ctx.setAccount(result.getAccount());
//							forward(result.getAccount());
//						}
//					}
//
//					@Override
//					public void onFailure(Throwable caught) {
//						if (caught instanceof NotFoundException) {
//							view.displayMessage(
//									LoginWidget.ACCOUNT_DOES_NOT_EXIST,
//									AlertType.ERROR);
//						} else if (caught instanceof InvalidCredentialsException) {
//							view.displayMessage(
//									LoginWidget.INVALID_ACCOUNT_CREDENTIALS,
//									AlertType.ERROR);
//						}
//						view.resetLoginForm();
//					}
//		});
//	}
//
//	@Override
//	public void setImageUploadUrl() {
//		dispatcher.execute(new GetImageUploadUrlAction(),
//				new DispatcherCallbackAsync<GetImageUploadUrlResult>() {
//					@Override
//					public void onSuccess(GetImageUploadUrlResult result) {
//						// TODO hack for making it work in local environment
//						String url = result.getImageUrl().replace(
//								"susnatas-MacBook-Pro.local:8888",
//								"127.0.0.1:8888");
//						System.out.println("Setting upload image form action to:"+url);
//						view.setImageUploadUrl(url);
//					}
//				});
//	}
//
//	// TODO handle image deletion on multiple file uploads
//	@Override
//	public void setUploadImageHandler() {
//		view.addUploadFormHandler(new FormPanel.SubmitCompleteHandler() {
//			@Override
//			public void onSubmitComplete(SubmitCompleteEvent event) {
//				String imageUrl = event.getResults();
//				System.out.println("Received uploaded image url:"+imageUrl);
//				view.displayProfileImagePreview(imageUrl);
//				view.reset();
//				setImageUploadUrl();
////				view.setImageUploadUrl(imageUrl);
////				profileImagePreview.setUrl(imageUrl);
////				profileImagePreview.setVisible(true);
////				SignupView.this.profileImageUrl = imageUrl;
//			}
//		});
//	}

}
