package com.ziplly.app.client.activities;

import java.util.List;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.exceptions.NeedsSubscriptionException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.exceptions.UsageLimitExceededException;
import com.ziplly.app.client.places.BusinessAccountPlace;
import com.ziplly.app.client.places.BusinessAccountSettingsPlace;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.view.BusinessAccountView;
import com.ziplly.app.client.view.IAccountView;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.event.TweetNotAvailableEvent;
import com.ziplly.app.client.view.handler.TweetNotAvailableEventHandler;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.SpamDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.shared.GetAccountByIdAction;
import com.ziplly.app.shared.GetAccountByIdResult;
import com.ziplly.app.shared.GetAccountDetailsResult;
import com.ziplly.app.shared.GetLatLngResult;
import com.ziplly.app.shared.GetTweetForUserAction;
import com.ziplly.app.shared.GetTweetForUserResult;
import com.ziplly.app.shared.ReportSpamResult;
import com.ziplly.app.shared.TweetResult;

public class BusinessAccountActivity extends AbstractAccountActivity<BusinessAccountDTO> implements
    InfiniteScrollHandler {

	private BusinessAccountPlace place;
	private AcceptsOneWidget panel;
	private int tweetPageIndex;
	private List<TweetDTO> lastTweetList;
	private TweetViewBinder binder;
	private ScrollBottomHitActionHandler scrollBottomHitHandler = new ScrollBottomHitActionHandler();
	private TweetHandler tweetHandler = new TweetHandler();
	private AsyncProvider<BusinessAccountView> viewProvider;

	public static interface IBusinessAccountView extends IAccountView<BusinessAccountDTO> {
		void displayFormattedAddress(String fAddress);
	}

	@Inject
	public BusinessAccountActivity(CachingDispatcherAsync dispatcher,
	    EventBus eventBus,
	    PlaceController placeController,
	    ApplicationContext ctx,
	    AsyncProvider<BusinessAccountView> viewProvider,
	    BusinessAccountPlace place) {

		super(dispatcher, eventBus, placeController, ctx, null);
		this.place = place;
		this.viewProvider = viewProvider;
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
	public void start(final AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		checkAccountLogin();
	}

	@Override
	public void doStart() {
		viewProvider.get(new DefaultViewLoaderAsyncCallback<BusinessAccountView>() {

			@Override
			public void onSuccess(BusinessAccountView result) {
				BusinessAccountActivity.this.view = result;
				bind();
				setupHandlers();
				go(BusinessAccountActivity.this.panel);
				if (place.getAccountId() != null) {
					displayPublicProfile(place.getAccountId());
				} else {
					displayProfile();
				}
			}
		});
	}

	@Override
	public void doStartOnUserNotLoggedIn() {
		viewProvider.get(new DefaultViewLoaderAsyncCallback<BusinessAccountView>() {

			@Override
			public void onSuccess(BusinessAccountView result) {
				BusinessAccountActivity.this.view = result;
				bind();
				setupHandlers();
				go(BusinessAccountActivity.this.panel);
				if (place.getAccountId() != null) {
					displayPublicProfile(place.getAccountId());
				} else {
					placeController.goTo(new LoginPlace());
				}
			}
		});
	}

	/*
	 * Display people's profile
	 */
	@Override
	public void displayPublicProfile(final Long accountId) {
		if (accountId != null) {
			dispatcher.execute(new GetAccountByIdAction(accountId), new GetAccountByIdActionHandler());
			fetchTweets(place.getAccountId(), tweetPageIndex, TWEETS_PER_PAGE, true);
			binder = new TweetViewBinder(view.getTweetSectionElement(), this);
			binder.start();
			getPublicAccountDetails(accountId, new GetPublicAccountDetailsActionHandler());
			go(panel);
		}
	}

	@Override
	public void displayProfile() {
		if (ctx.getAccount() instanceof PersonalAccountDTO) {
			placeController.goTo(new PersonalAccountPlace());
			return;
		}

		view.displayProfile((BusinessAccountDTO) ctx.getAccount());

		// Display target neighborhood
		view.displayTargetNeighborhoods(getTargetNeighborhoodList());

		fetchTweets(ctx.getAccount().getAccountId(), tweetPageIndex, TWEETS_PER_PAGE, false);
		getAccountNotifications();
		binder = new TweetViewBinder(view.getTweetSectionElement(), this);
		binder.start();
		getLatLng(ctx.getAccount(), new GetLatLngResultHandler());
		getAccountDetails(new GetAccountDetailsActionHandler());
		setupImageUpload();
		view.displayAccontUpdate();
	}

	private void setupImageUpload() {
		setImageUploadUrl();
		setUploadImageHandler();
	}

	@Override
	public void settingsLinkClicked() {
		placeController.goTo(new BusinessAccountSettingsPlace());
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
			action =
			    new GetTweetForUserAction(
			        ctx.getAccount().getAccountId(),
			        tweetPageIndex,
			        TWEETS_PER_PAGE);
		}
		dispatcher.execute(action, scrollBottomHitHandler);
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
	public DispatcherCallbackAsync<TweetResult> getTweetHandler() {
		return tweetHandler;
	}

	@Override
	public void onStop() {
		view.clear();
	}

	@Override
	public void reportTweetAsSpam(TweetDTO tweet) {
		SpamDTO spam = new SpamDTO();
		spam.setTweet(tweet);
		spam.setReporter(ctx.getAccount());
		reportSpam(spam, new ReportSpamActionHandler());
	}

	private class GetLatLngResultHandler extends DispatcherCallbackAsync<GetLatLngResult> {
		@Override
		public void onSuccess(GetLatLngResult result) {
			if (result.getFormattedAddress() != null) {
				view.displayLocationInMap(result);
				((BusinessAccountView) view).displayFormattedAddress(result.getFormattedAddress());
			}
		}

		@Override
		public void onFailure(Throwable th) {
			Window.alert(th.getMessage());
		}
	}

	private class GetAccountByIdActionHandler extends DispatcherCallbackAsync<GetAccountByIdResult> {

		@Override
		public void onSuccess(GetAccountByIdResult result) {
			AccountDTO account = result.getAccount();
			if (account instanceof BusinessAccountDTO) {
				view.displayPublicProfile((BusinessAccountDTO) account);
				getLatLng(account, new GetLatLngResultHandler());
			} else if (account instanceof PersonalAccountDTO) {
				// take some action here
				placeController.goTo(new PersonalAccountPlace(account.getAccountId()));
			}
		}

		@Override
		public void onFailure(Throwable th) {
			if (th instanceof NotFoundException) {
				view.displayModalMessage(StringConstants.NO_ACCOUNT_FOUND, AlertType.ERROR);
			} else {
				view.displayModalMessage(StringConstants.INTERNAL_ERROR, AlertType.ERROR);
			}
			view.hideProfileSection();
		}
	}

	private class GetAccountDetailsActionHandler extends
	    DispatcherCallbackAsync<GetAccountDetailsResult> {

		@Override
		public void onSuccess(GetAccountDetailsResult result) {
			onAccountDetailsUpdate(result);
		}
	}

	private class GetPublicAccountDetailsActionHandler extends
	    DispatcherCallbackAsync<GetAccountDetailsResult> {

		@Override
		public void onSuccess(GetAccountDetailsResult result) {
			// onAccountDetailsUpdate(result);
			view.updatePublicAccountDetails(result);
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
			if (th instanceof NeedsSubscriptionException) {
				view.displayModalMessage(th.getMessage(), AlertType.ERROR);
			} else if (th instanceof UsageLimitExceededException) {
				view.displayModalMessage(StringConstants.USAGE_LIMIT_EXCEEDED_EXCEPTION, AlertType.ERROR);
			} else {
				view.displayModalMessage(StringConstants.INTERNAL_ERROR, AlertType.ERROR);
			}
		}
	}

	private class ReportSpamActionHandler extends DispatcherCallbackAsync<ReportSpamResult> {
		@Override
		public void onSuccess(ReportSpamResult result) {
			view.displayModalMessage(StringConstants.REPORT_SPAM_SUCCESSFUL, AlertType.SUCCESS);
		}
	}

	@Override
	void stopThreads() {
		if (binder != null) {
			binder.stop();
		}
	}

	private class ScrollBottomHitActionHandler extends DispatcherCallbackAsync<GetTweetForUserResult> {
		@Override
		public void onSuccess(GetTweetForUserResult result) {
			lastTweetList = result.getTweets();
			view.addTweets(result.getTweets());
		}
	}
}
