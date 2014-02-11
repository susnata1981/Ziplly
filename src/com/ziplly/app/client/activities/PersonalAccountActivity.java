package com.ziplly.app.client.activities;

import java.util.List;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.exceptions.AccessError;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.places.BusinessAccountPlace;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.places.PersonalAccountSettingsPlace;
import com.ziplly.app.client.view.AccountView;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.event.LoadingEventEnd;
import com.ziplly.app.client.view.event.TweetNotAvailableEvent;
import com.ziplly.app.client.view.handler.TweetNotAvailableEventHandler;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.SpamDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.shared.DeleteTweetAction;
import com.ziplly.app.shared.DeleteTweetResult;
import com.ziplly.app.shared.GetAccountByIdAction;
import com.ziplly.app.shared.GetAccountByIdResult;
import com.ziplly.app.shared.GetAccountDetailsResult;
import com.ziplly.app.shared.GetLatLngResult;
import com.ziplly.app.shared.GetTweetForUserAction;
import com.ziplly.app.shared.GetTweetForUserResult;
import com.ziplly.app.shared.ReportSpamResult;
import com.ziplly.app.shared.TweetResult;

public class PersonalAccountActivity extends AbstractAccountActivity<PersonalAccountDTO> implements InfiniteScrollHandler {
	private PersonalAccountPlace place;
	private AcceptsOneWidget panel;
	private int tweetPageIndex;
	private List<TweetDTO> lastTweetList;
	private TweetViewBinder binder;
	private ScrollBottomHitActionHandler scrollBottomHitActionHandler = new ScrollBottomHitActionHandler();

	@Inject
	public PersonalAccountActivity(CachingDispatcherAsync dispatcher, EventBus eventBus,
			PlaceController placeController, ApplicationContext ctx, AccountView view,
			PersonalAccountPlace place) {

		super(dispatcher, eventBus, placeController, ctx, view);
		this.place = place;
		setupHandlers();
	}
	
	@Override
	protected void setupHandlers() {
		super.setupHandlers();
		eventBus.addHandler(TweetNotAvailableEvent.TYPE, new TweetNotAvailableEventHandler() {
			
			@Override
			public void onEvent(TweetNotAvailableEvent event) {
				if (binder != null) {
					binder.stop();
				}
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
		} else if (ctx.getAccount() != null) {
			displayProfile();
			go(panel);
		} else {
			goTo(new LoginPlace());
		}
	}

	/**
	 * Display people's profile
	 */
	@Override
	public void displayPublicProfile(final Long accountId) {
		if (accountId != null) {
			dispatcher.execute(new GetAccountByIdAction(accountId),
					new GetAccountByIdActionHandler());
			fetchTweets(place.getAccountId(), tweetPageIndex, TWEETS_PER_PAGE, true);
			startInfiniteScrollThread();
			getPublicAccountDetails(accountId, new GetPublicAccountDetailsActionHandler());
			go(panel);
		}
	}

	private void startInfiniteScrollThread() {
		if (binder != null) {
			binder.stop();
		}
		binder = new TweetViewBinder(view.getTweetSectionElement(), this);
		binder.start();
	}

	@Override
	public void displayProfile() {
		if (ctx.getAccount() instanceof BusinessAccountDTO) {
			placeController.goTo(new BusinessAccountPlace());
			return;
		}

		view.displayProfile((PersonalAccountDTO) ctx.getAccount());
		view.displayTargetNeighborhoods(getTargetNeighborhoodList());
		fetchTweets(ctx.getAccount().getAccountId(), tweetPageIndex, TWEETS_PER_PAGE, false);
		startInfiniteScrollThread();
		getLatLng(ctx.getAccount(), new GetLatLngResultHandler());
		getAccountDetails(new GetAccountDetailsActionHandler());
		getAccountNotifications();
		setupImageUpload();
	}

	private void setupImageUpload() {
		setImageUploadUrl();
		setUploadImageHandler();
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
		return lastTweetList.size() == TWEETS_PER_PAGE;
	}

	/*
	 * For InfiniteScrollHandler interface
	 */
	@Override
	public void onScrollBottomHit() {
		tweetPageIndex++;
		GetTweetForUserAction action = null;
		if (place.getAccountId() != null) {
			action = new GetTweetForUserAction(place.getAccountId(), tweetPageIndex, TWEETS_PER_PAGE);
		} else if (ctx.getAccount() != null) {
			action = new GetTweetForUserAction(ctx.getAccount().getAccountId(), tweetPageIndex,
					TWEETS_PER_PAGE);
		}

		dispatcher.execute(action, scrollBottomHitActionHandler);
	}

	@Override
	public void reportTweetAsSpam(TweetDTO tweet) {
		SpamDTO spam = new SpamDTO();
		spam.setTweet(tweet);
		spam.setReporter(ctx.getAccount());
		reportSpam(spam, new ReportSpamActionHandler());
	}

	/**
	 * Called in response to AccountDetailsUpdateEvent event
	 * 
	 * @param result
	 */
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
		if (binder != null) {
			binder.stop();
		}
		eventBus.fireEvent(new LoadingEventEnd());
		view.clearTweet();
	}

	private class GetLatLngResultHandler extends DispatcherCallbackAsync<GetLatLngResult> {
		@Override
		public void onSuccess(GetLatLngResult result) {
			if (result != null) {
				PersonalAccountActivity.this.view.displayLocationInMap(result);
			}
		}
	}

	private class GetAccountByIdActionHandler extends DispatcherCallbackAsync<GetAccountByIdResult> {

		@Override
		public void onSuccess(GetAccountByIdResult result) {
			AccountDTO account = result.getAccount();
			if (account instanceof PersonalAccountDTO) {
				view.displayPublicProfile((PersonalAccountDTO) account);
				getLatLng(account, new GetLatLngResultHandler());
			} else {
				// take some action here
				placeController.goTo(new BusinessAccountPlace(account.getAccountId()));
			}
		}

		@Override
		public void onFailure(Throwable th) {
			if (th instanceof NotFoundException) {
				view.displayMessage(StringConstants.NO_ACCOUNT_FOUND, AlertType.ERROR);
				view.hideProfileSection();
			} else {
				view.displayMessage(StringConstants.INTERNAL_ERROR, AlertType.ERROR);
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

	@Override
	public DispatcherCallbackAsync<TweetResult> getTweetHandler() {
		return new TweetHandler();
	}

	@Override
	public void deleteTweet(final TweetDTO tweet) {
		if (ctx.getAccount().getAccountId() != tweet.getSender().getAccountId()) {
			view.displayMessage(StringConstants.INVALID_ACCESS, AlertType.ERROR);
		}

		dispatcher.execute(new DeleteTweetAction(tweet.getTweetId()),
				new DispatcherCallbackAsync<DeleteTweetResult>() {
					@Override
					public void onSuccess(DeleteTweetResult result) {
						view.displayMessage(StringConstants.TWEET_REMOVED, AlertType.SUCCESS);
						view.removeTweet(tweet);
					}

					@Override
					public void onFailure(Throwable th) {
						if (th instanceof AccessError) {
							view.displayMessage(StringConstants.INVALID_ACCESS, AlertType.ERROR);
						} else {
							view.displayMessage(StringConstants.INTERNAL_ERROR, AlertType.ERROR);
						}
					}
				});
	}

	private class GetPublicAccountDetailsActionHandler extends
			DispatcherCallbackAsync<GetAccountDetailsResult> {

		@Override
		public void onSuccess(GetAccountDetailsResult result) {
			onAccountDetailsUpdate(result);
		}
	}

	private class TweetHandler extends DispatcherCallbackAsync<TweetResult> {
		@Override
		public void onSuccess(TweetResult result) {
			placeController.goTo(new HomePlace());
			view.clearTweet();
		}

		@Override
		public void onFailure(Throwable th) {
			view.displayMessage(StringConstants.INTERNAL_ERROR, AlertType.ERROR);
		}
	}

	private class ReportSpamActionHandler extends DispatcherCallbackAsync<ReportSpamResult> {
		@Override
		public void onSuccess(ReportSpamResult result) {
			view.displayMessage(StringConstants.REPORT_SPAM_SUCCESSFUL, AlertType.SUCCESS);
		}
	}

	private class ScrollBottomHitActionHandler extends	DispatcherCallbackAsync<GetTweetForUserResult> {
		@Override
		public void onSuccess(GetTweetForUserResult result) {
			lastTweetList = result.getTweets();
			view.addTweets(result.getTweets());
		}
	}

	@Override
	void stopThreads() {
		if (binder != null) {
			binder.stop();
		}
	}
}
