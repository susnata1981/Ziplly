package com.ziplly.app.client.activities;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.AccountPlace;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.view.AccountView;
import com.ziplly.app.client.view.event.AccountUpdateEvent;
import com.ziplly.app.client.view.event.LogoutEvent;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.shared.GetAccountByIdAction;
import com.ziplly.app.shared.GetAccountByIdResult;
import com.ziplly.app.shared.GetLoggedInUserAction;
import com.ziplly.app.shared.GetLoggedInUserResult;
import com.ziplly.app.shared.LogoutAction;
import com.ziplly.app.shared.LogoutResult;
import com.ziplly.app.shared.TweetAction;
import com.ziplly.app.shared.TweetResult;
import com.ziplly.app.shared.UpdateAccountAction;
import com.ziplly.app.shared.UpdateAccountResult;

public class AccountActivity extends AbstractLoginAwareActivity<AccountView>
		implements AccountActivityPresenter {
	
	AccountPlace place;
	protected AccountDTO account;
	private Logger logger = Logger.getLogger(AccountActivity.class.getName());
	private AcceptsOneWidget panel;

	@Inject
	public AccountActivity(CachingDispatcherAsync dispatcher,
			EventBus eventBus, AccountPlace place,
			PlaceController placeController, AccountView accountView) {
		super(dispatcher, eventBus, placeController, accountView);
		this.place = place;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		bind();
		fetchData();
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
	public void updateUi() {
		go(panel);
	}

	@Override
	public void onStop() {
		view.clear();
	}

	@Override
	public void fetchData() {
		final Long accountId = place.getAccountId();
		if ( accountId != null) {
			dispatcher.execute(new GetAccountByIdAction(accountId), new DispatcherCallbackAsync<GetAccountByIdResult>() {
				@Override
				public void onSuccess(GetAccountByIdResult result) {
					if (result == null || result.getAccount() == null) {
						logger.log(Level.WARNING, "Accessing invalid account id "+accountId);
						// TODO display an error page; 
						Window.alert("Invalid account");
						return;
					} 
					displayPublicProfile(result.getAccount());
					updateUi();
				}
			});
		} else {
			dispatcher.execute(new GetLoggedInUserAction(),
					new DispatcherCallbackAsync<GetLoggedInUserResult>() {
						@Override
						public void onSuccess(GetLoggedInUserResult result) {
							if (result != null && result.getAccount() != null) {
								AccountActivity.this.displayPersonalProfile(result.getAccount());
							} else {
								System.out.println("Display login widget...");
								view.displayLoginWidget();
							}
							updateUi();
						}
					});
		}
	}

	/*
	 * displays personal profile (only to be called for logged in user)
	 */
	private void displayPersonalProfile(AccountDTO account) {
		this.account = account;
		view.display(account);
		view.displayLogoutWidget();
	}
	
	/*
	 * displays public profile (called by AccountActivity)
	 */
	private void displayPublicProfile(AccountDTO account) {
		view.displayPublicProfile(account);
	}
	
	/*
	 * displays public profile (called by the view)
	 */
	@Override
	public void displayPublicProfile(Long accountId) {
		placeController.goTo(new AccountPlace(accountId));
	}
	
	@Override
	public void logout() {
		dispatcher.execute(new LogoutAction(account.getUid()),
				new DispatcherCallbackAsync<LogoutResult>() {
					@Override
					public void onSuccess(LogoutResult result) {
						eventBus.fireEvent(new LogoutEvent());
						view.clear();
						goTo(new HomePlace());
					}
				});
	}

	@Override
	public void onLogin(String email, String password) {
		validateLogin(email, password);
	}

	@Override
	public void save(AccountDTO account) {
		dispatcher.execute(new UpdateAccountAction(account),
				new DispatcherCallbackAsync<UpdateAccountResult>() {
					@Override
					public void onSuccess(UpdateAccountResult result) {
						view.displayAccountUpdateSuccessfullMessage();
						eventBus.fireEvent(new AccountUpdateEvent(result
								.getAccount()));
					}

					public void onFailure(Throwable error) {
						System.out.println(error.getMessage());
						view.displayAccountUpdateFailedMessage();
					}
				});
	}

	public void tweet(String content) {
		if (account == null) {
			placeController.goTo(new AccountPlace());
		}
		TweetDTO tweet = new TweetDTO();
		tweet.setContent(content);
		tweet.setSender(account);
		tweet.setTimeCreated(new Date());
		tweet.setType(TweetType.GENERAL);
		dispatcher.execute(new TweetAction(tweet), new DispatcherCallbackAsync<TweetResult>() {
			@Override
			public void onSuccess(TweetResult result) {
				placeController.goTo(new HomePlace());
				AccountActivity.this.view.clearTweet();
			}
		});
	}
}
