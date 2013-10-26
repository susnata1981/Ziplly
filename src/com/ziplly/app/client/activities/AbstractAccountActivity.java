package com.ziplly.app.client.activities;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.view.IAccountView;
import com.ziplly.app.client.view.event.AccountUpdateEvent;
import com.ziplly.app.client.view.event.LogoutEvent;
import com.ziplly.app.client.view.handler.AccountUpdateEventHandler;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.shared.GetAccountByIdAction;
import com.ziplly.app.shared.GetAccountByIdResult;
import com.ziplly.app.shared.GetImageUploadUrlAction;
import com.ziplly.app.shared.GetImageUploadUrlResult;
import com.ziplly.app.shared.LogoutAction;
import com.ziplly.app.shared.LogoutResult;
import com.ziplly.app.shared.TweetAction;
import com.ziplly.app.shared.TweetResult;
import com.ziplly.app.shared.UpdateAccountAction;
import com.ziplly.app.shared.UpdateAccountResult;

public abstract class AbstractAccountActivity<T extends AccountDTO> extends AbstractActivity implements AccountPresenter<T> {
	protected IAccountView<T> view;

	public AbstractAccountActivity(CachingDispatcherAsync dispatcher,
			EventBus eventBus, 
			PlaceController placeController,
			ApplicationContext ctx,
			IAccountView<T> view) {
		super(dispatcher, eventBus, placeController, ctx);
		this.view = view;
		eventBus.addHandler(AccountUpdateEvent.TYPE, new AccountUpdateEventHandler() {
			@Override
			public void onEvent(AccountUpdateEvent event) {
				AbstractAccountActivity.this.ctx.setAccount(event.getAccount());
				displayProfile();
			}
		});
	}

	@Override
	public void fetchData() {
	}
	
	@Override
	public void save(T account) {
		dispatcher.execute(new UpdateAccountAction(account),
				new DispatcherCallbackAsync<UpdateAccountResult>() {
					@Override
					public void onSuccess(UpdateAccountResult result) {
						view.displayAccountUpdateSuccessfullMessage();
						eventBus.fireEvent(new AccountUpdateEvent(result
								.getAccount()));
					}

					public void onFailure(Throwable error) {
						view.displayAccountUpdateFailedMessage();
					}
				});
	}
	
	@Override
	public void tweet(TweetDTO tweet) {
		AccountDTO account = ctx.getAccount();
		if (account == null) {
			placeController.goTo(new LoginPlace());
		}
		
		dispatcher.execute(new TweetAction(tweet),
				new DispatcherCallbackAsync<TweetResult>() {
					@Override
					public void onSuccess(TweetResult result) {
						placeController.goTo(new HomePlace());
						view.clearTweet();
					}
				});
	}

	@Override
	public void logout() {
		dispatcher.execute(new LogoutAction(ctx.getAccount().getUid()),
				new DispatcherCallbackAsync<LogoutResult>() {
					@Override
					public void onSuccess(LogoutResult result) {
						eventBus.fireEvent(new LogoutEvent());
						ctx.setAccount(null);
						goTo(new LoginPlace());
					}
				});
	}

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
	public void setUploadImageHandler() {
		view.addUploadFormHandler(new FormPanel.SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				String imageUrl = event.getResults();
				System.out.println("Received uploaded image url:"+imageUrl);
				view.displayProfileImagePreview(imageUrl);
				view.resetUploadForm();
				setImageUploadUrl();
			}
		});
	}
	
	@Override
	public void displayPublicProfile(T account) {
		view.displayPublicProfile(account);
	}
}
