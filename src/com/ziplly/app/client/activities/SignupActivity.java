package com.ziplly.app.client.activities;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.inject.Inject;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.AccountPlace;
import com.ziplly.app.client.places.SignupPlace;
import com.ziplly.app.client.view.SignupView;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.shared.GetImageUploadUrlAction;
import com.ziplly.app.shared.GetImageUploadUrlResult;
import com.ziplly.app.shared.RegisterAccountAction;
import com.ziplly.app.shared.RegisterAccountResult;

public class SignupActivity extends AbstractLoginAwareActivity<SignupView>
		implements SignupActivityPresenter {
	private SignupPlace place;

	@Inject
	public SignupActivity(CachingDispatcherAsync dispatcher, EventBus eventBus,
			PlaceController placeController, SignupPlace place, SignupView view) {
		super(dispatcher, eventBus, placeController, view);
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
	public void updateUi() {
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

	@Override
	public void register(AccountDTO account) {
		dispatcher.execute(new RegisterAccountAction(account),
				new DispatcherCallbackAsync<RegisterAccountResult>() {
					@Override
					public void onSuccess(RegisterAccountResult result) {
						System.out.println("Account " + result.getAccount()
								+ " registered.");
						eventBus.fireEvent(new LoginEvent(result.getAccount()));
						placeController.goTo(new AccountPlace());
					}
				});
	}

	@Override
	public void onLogin(String email, String password) {
		validateLogin(email, password);
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
