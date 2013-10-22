package com.ziplly.app.client.activities;

import java.util.Date;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.AccountPlace;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.view.AccountView;
import com.ziplly.app.client.view.event.AccountUpdateEvent;
import com.ziplly.app.client.view.event.LogoutEvent;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.shared.LogoutAction;
import com.ziplly.app.shared.LogoutResult;
import com.ziplly.app.shared.TweetAction;
import com.ziplly.app.shared.TweetResult;
import com.ziplly.app.shared.UpdateAccountAction;
import com.ziplly.app.shared.UpdateAccountResult;

public class PersonalAccountPresenter implements AccountActivityPresenter<PersonalAccountDTO>{
	private AccountView view;
	private AccountDTO account;
	private CachingDispatcherAsync dispatcher;
	private EventBus eventBus;
	private PlaceController placeController;

	public PersonalAccountPresenter(
			CachingDispatcherAsync dispatcher,
			EventBus eventBus, 
			PlaceController placeController,
			AccountView accountView) {
		this.dispatcher = dispatcher;
		this.eventBus = eventBus;
		this.placeController = placeController;
		this.view = accountView;
	}

	@Override
	public void save(PersonalAccountDTO account) {
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

	@Override
	public void displayProfile(PersonalAccountDTO account) {
		view.displayPublicProfile(account);
	}

	@Override
	public void displayPublicProfile(Long accountId) {
		placeController.goTo(new AccountPlace(accountId));
	}

	@Override
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
				PersonalAccountPresenter.this.view.clearTweet();
			}
		});
	}

	@Override
	public void logout() {
		dispatcher.execute(new LogoutAction(account.getUid()),
				new DispatcherCallbackAsync<LogoutResult>() {
					@Override
					public void onSuccess(LogoutResult result) {
						eventBus.fireEvent(new LogoutEvent());
						view.clear();
						placeController.goTo(new HomePlace());
					}
				});
	}

}
