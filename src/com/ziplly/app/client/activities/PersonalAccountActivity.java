package com.ziplly.app.client.activities;

import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.BusinessAccountPlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.places.PersonalAccountSettingsPlace;
import com.ziplly.app.client.view.AccountView;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.view.handler.LoginEventHandler;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.shared.GetAccountByIdAction;
import com.ziplly.app.shared.GetAccountByIdResult;
import com.ziplly.app.shared.GetAccountDetailsResult;
import com.ziplly.app.shared.GetLatLngResult;
import com.ziplly.app.shared.GetTweetForUserAction;
import com.ziplly.app.shared.GetTweetForUserResult;

public class PersonalAccountActivity extends
		AbstractAccountActivity<PersonalAccountDTO> implements
		InfiniteScrollHandler {
	private PersonalAccountPlace place;
	private AcceptsOneWidget panel;
	private int tweetPageIndex;
	private int pageSize = 3;
	private List<TweetDTO> lastTweetList;

	@Inject
	public PersonalAccountActivity(CachingDispatcherAsync dispatcher,
			EventBus eventBus, PlaceController placeController,
			ApplicationContext ctx, AccountView view, PersonalAccountPlace place) {
		
		super(dispatcher, eventBus, placeController, ctx, view);
		this.place = place;
		eventBus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {
			@Override
			public void onEvent(LoginEvent event) {
				System.out.println("User " + event.getAccount() + " logged in");
			}
		});
	}

	@Override
	public void bind() {
		view.setPresenter(this);
	}

	@Override
	public void go(AcceptsOneWidget container) {
		container.setWidget(view);
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		bind();
		
		if (place.getAccountId() != null) {
			displayPublicProfile(place.getAccountId());
		}
		else if (ctx.getAccount() != null) {
			displayProfile();
			go(panel);
		} 
		else {
			goTo(new LoginPlace());
		}
	}

	/*
	 * Display people's profile
	 */
	@Override
	public void displayPublicProfile(final Long accountId) {
		if (accountId != null) {
			fetchTweets(place.getAccountId(), tweetPageIndex, pageSize);
			TweetViewBinder binder = new TweetViewBinder(
					view.getTweetSectionElement(), this);
			binder.start();
			dispatcher.execute(new GetAccountByIdAction(accountId),
					new GetAccountByIdActionHandler());
		}
	}

	@Override
	public void displayProfile() {
		fetchTweets(ctx.getAccount().getAccountId(), tweetPageIndex,
				pageSize);
		TweetViewBinder binder = new TweetViewBinder(
				view.getTweetSectionElement(), this);
		binder.start();
		getLatLng(ctx.getAccount().getZip(), new GetLatLngResultHandler());
		displayProfile();
		getAccountDetails(new GetAccountDetailsActionHandler());
		view.displayProfile((PersonalAccountDTO) ctx.getAccount());
	}

	@Override
	public void settingsLinkClicked() {
		placeController.goTo(new PersonalAccountSettingsPlace());
	}

	/*
	 * For InfiniteScrollHandler interface
	 */
	@Override
	public boolean hasMoreElements() {
		if (lastTweetList == null) {
			return true;
		}
		return lastTweetList.size() == pageSize;
	}

	/*
	 * For InfiniteScrollHandler interface
	 */
	@Override
	public void onScrollBottomHit() {
		tweetPageIndex++;
		GetTweetForUserAction action = null;
		if (place.getAccountId() != null) {
			action = new GetTweetForUserAction(place.getAccountId(),
					tweetPageIndex, pageSize);
		} else {
			action = new GetTweetForUserAction(ctx.getAccount().getAccountId(),
					tweetPageIndex, pageSize);
		}
		dispatcher.execute(action,
				new DispatcherCallbackAsync<GetTweetForUserResult>() {

					@Override
					public void onSuccess(GetTweetForUserResult result) {
						lastTweetList = result.getTweets();
						view.addTweets(result.getTweets());
					}
				});
	}

	protected void onAccountDetailsUpdate(GetAccountDetailsResult result) {
		ctx.setUnreadMessageCount(result.getUnreadMessages());
		ctx.setTotalTweets(result.getTotalTweets());
		ctx.setTotalComments(result.getTotalComments());
		ctx.setTotalLikes(result.getTotalLikes());
		view.updateAccountDetails(ctx);
	}

	@Override
	protected void onAccountDetailsUpdate() {
		getAccountDetails(new DispatcherCallbackAsync<GetAccountDetailsResult>() {
			@Override
			public void onSuccess(GetAccountDetailsResult result) {
				onAccountDetailsUpdate(result);
			}
		});
	}

	@Override
	public void onStop() {
		// empty for now
	}

	private class GetLatLngResultHandler extends
			DispatcherCallbackAsync<GetLatLngResult> {
		@Override
		public void onSuccess(GetLatLngResult result) {
			PersonalAccountActivity.this.view.displayLocationInMap(result);
		}
	}

	private class GetAccountByIdActionHandler extends
			DispatcherCallbackAsync<GetAccountByIdResult> {

		@Override
		public void onSuccess(GetAccountByIdResult result) {
			AccountDTO account = result.getAccount();
			if (account instanceof PersonalAccountDTO) {
				view.displayPublicProfile((PersonalAccountDTO) account);
				getLatLng(account.getZip(), new GetLatLngResultHandler());
				go(panel);
			} else {
				// take some action here
				placeController.goTo(new BusinessAccountPlace(account
						.getAccountId()));
			}
		}
	}

	private class GetAccountDetailsActionHandler extends
			DispatcherCallbackAsync<GetAccountDetailsResult> {

		@Override
		public void onSuccess(GetAccountDetailsResult result) {
			onAccountDetailsUpdate(result);
		}

	}
}
