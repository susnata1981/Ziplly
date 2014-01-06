package com.ziplly.app.client.activities;

import java.util.List;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.exceptions.AccessError;
import com.ziplly.app.client.exceptions.DuplicateException;
import com.ziplly.app.client.exceptions.InvalidCredentialsException;
import com.ziplly.app.client.exceptions.NeedsSubscriptionException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.exceptions.UsageLimitExceededException;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.view.HomeView;
import com.ziplly.app.client.view.HomeView.HomePresenter;
import com.ziplly.app.client.view.MainView;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.View;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.widget.LoginWidget;
import com.ziplly.app.client.widget.TweetWidget;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.HashtagDTO;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.SpamDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.shared.CommentAction;
import com.ziplly.app.shared.CommentResult;
import com.ziplly.app.shared.DeleteTweetAction;
import com.ziplly.app.shared.DeleteTweetResult;
import com.ziplly.app.shared.GetAccountNotificationAction;
import com.ziplly.app.shared.GetCommunityWallDataAction;
import com.ziplly.app.shared.GetCommunityWallDataResult;
import com.ziplly.app.shared.GetFacebookRedirectUriResult;
import com.ziplly.app.shared.GetHashtagAction;
import com.ziplly.app.shared.GetHashtagResult;
import com.ziplly.app.shared.GetLatLngAction;
import com.ziplly.app.shared.GetLatLngResult;
import com.ziplly.app.shared.GetLoggedInUserAction;
import com.ziplly.app.shared.GetLoggedInUserResult;
import com.ziplly.app.shared.GetNeighborhoodDetailsAction;
import com.ziplly.app.shared.GetNeighborhoodDetailsResult;
import com.ziplly.app.shared.GetTweetCategoryDetailsAction;
import com.ziplly.app.shared.GetTweetCategoryDetailsResult;
import com.ziplly.app.shared.LikeResult;
import com.ziplly.app.shared.LikeTweetAction;
import com.ziplly.app.shared.ReportSpamAction;
import com.ziplly.app.shared.ReportSpamResult;
import com.ziplly.app.shared.SendMessageAction;
import com.ziplly.app.shared.SendMessageResult;
import com.ziplly.app.shared.TweetAction;
import com.ziplly.app.shared.TweetResult;
import com.ziplly.app.shared.UpdateTweetAction;
import com.ziplly.app.shared.UpdateTweetResult;
import com.ziplly.app.shared.ValidateLoginAction;
import com.ziplly.app.shared.ValidateLoginResult;

public class HomeActivity extends AbstractActivity implements HomePresenter, InfiniteScrollHandler {
	private MainView mainView;
	private IHomeView homeView;
	private HomeViewState state;
	private TweetViewBinder binder;
	private HomePlace place;
	private AccountDTO account;
	
//	private HTMLPanel loadingPanel = new HTMLPanel("<span>Loading</span>");
	private AcceptsOneWidget panel;

	private AccountNotificationHandler accountNotificationHandler = new AccountNotificationHandler();
	private CommunityDataHandler communityDataHandler = new CommunityDataHandler();
	private GetLoggedInUserActionHandler getLoggedInUserActionHandler = new GetLoggedInUserActionHandler();
	private GetNeighborhoodDetailsHandler neighborhoodDetailsHandler = new GetNeighborhoodDetailsHandler();
//	private GetFacebookRedirectUriHandler facebookRedirectHandler = new GetFacebookRedirectUriHandler();
	
	public static interface IHomeView extends View<HomePresenter> {
		void display(List<TweetDTO> tweets);

		void updateTweet(TweetDTO tweet);

		void removeTweet(TweetDTO tweet);

		void updateComment(CommentDTO comment);

		void updateTweetLike(LoveDTO like);

		void updateTweets(List<TweetDTO> tweets);

		Element getTweetSectionElement();

		void displayMessage(String message, AlertType error);

		void addTweet(TweetDTO tweet);

		void addTweets(List<TweetDTO> tweets);

		void displayHashtag(List<HashtagDTO> hashtags);

		void displayMap(GetLatLngResult result);

		void updateTweetCategoryCount(Map<TweetType, Integer> tweetCounts);

		void insertTweet(TweetDTO tweet);

		void displaySummaryData(NeighborhoodDTO neighborhood);

		void displayResidentCount(int totalResidents);

		void highlightTweetType(TweetType type);
	}

	@Inject
	public HomeActivity(CachingDispatcherAsync dispatcher, EventBus eventBus, HomePlace place,
			PlaceController placeController, MainView mainView, ApplicationContext ctx,
			HomeView homeView) {

		super(dispatcher, eventBus, placeController, ctx);
		this.place = place;
		this.homeView = homeView;
		this.mainView = mainView;
		state = new HomeViewState();
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
//		setBackgroundImage();
		bind();
		showLodingIcon();
		if (ctx.getAccount() != null) {
			displayCommunityWall();
		} else {
			fetchData();
			panel.setWidget(mainView);
		}
	}

	/**
	 * Loads the following data into home view
	 * 1. Tweets
	 * 2. Community Summary
	 * 3. Location
	 * 4. Top hashtags
	 * 5. Counts on Tweet types
	 */
	private void displayCommunityWall() {
		GetCommunityWallDataAction searchCriteria = state.getSearchCriteria(place);
		getCommunityWallData(searchCriteria);
		homeView.displaySummaryData(ctx.getAccount().getNeighborhood());
		getLatLng(ctx.getAccount());
		getHashtagList();
		GetTweetCategoryDetails();
		getNeighborhoodDetails();
	}

	private void getNeighborhoodDetails() {
		dispatcher.execute(new GetNeighborhoodDetailsAction(), neighborhoodDetailsHandler);
	}

	private void GetTweetCategoryDetails() {
		dispatcher.execute(new GetTweetCategoryDetailsAction(), new DispatcherCallbackAsync<GetTweetCategoryDetailsResult>() {
			@Override
			public void onSuccess(GetTweetCategoryDetailsResult result) {
				// do something.
				homeView.updateTweetCategoryCount(result.getTweetCounts());
			}
		});
	}

	private void getHashtagList() {
		dispatcher.execute(new GetHashtagAction(), new DispatcherCallbackAsync<GetHashtagResult>() {

			@Override
			public void onSuccess(GetHashtagResult result) {
				homeView.displayHashtag(result.getHashtags());
			}
		});
	}

	@Override
	public void onStop() {
		clearBackgroundImage();
		if (binder != null) {
			binder.stop();
		}
		hideLoadingIcon();
	}

	@Override
	public void go(AcceptsOneWidget container) {
		if (this.account != null) {
			displayHomeView();
		} else {
			fetchData();
		}
		hideLoadingIcon();
	}

	@Override
	public void bind() {
		mainView.setPresenter(this);
		homeView.setPresenter(this);
	}

	void displayMainView() {
		panel.setWidget(mainView);
	}

	void displayHomeView() {
		hideLoadingIcon();
		panel.setWidget(homeView);
	}

	@Override
	public void fetchData() {
//		dispatcher.execute(new GetFacebookRedirectUriAction(), facebookRedirectHandler);
		dispatcher.execute(new GetLoggedInUserAction(), getLoggedInUserActionHandler);
	}
	
	void getCommunityWallData(TweetType type) {
		showLodingIcon();
		GetCommunityWallDataAction searchCriteria = state.getSearchCriteriaForTweetType(type);
		state.setFetchingData(true);
		dispatcher.execute(searchCriteria, communityDataHandler);
		
		if (binder != null) {
			binder.stop();
		}
		
		binder = getDefaultTweetBinder();
		binder.start();
	}

	void getCommunityWallData(GetCommunityWallDataAction action) {
		// load first batch of data
		showLodingIcon();
		state.setFetchingData(true);
		dispatcher.execute(action, communityDataHandler);
		
		if (binder != null) {
			binder.stop();
		}
		
		binder = getDefaultTweetBinder();
		binder.start();
	}
	
	@Override
	public void displayTweets(List<TweetDTO> tweets) {
		homeView.display(tweets);
		panel.setWidget(homeView);
	}

	@Override
	public void displayTweetsForCategory(TweetType type) {
		getCommunityWallData(type);
	}

	@Override
	public void displayPublicProfile(Long accountId) {
		placeController.goTo(new LoginPlace(accountId));
	}

	@Override
	public void postComment(final CommentDTO comment) {
		dispatcher.execute(new CommentAction(comment), new PostCommentHandler(comment));
	}

	@Override
	public void likeTweet(Long tweetId) {
		LikeTweetAction action = new LikeTweetAction();
		action.setTweetId(tweetId);
		dispatcher.execute(action, new TweetLikeActionHandler());
	}

	@Override
	public void updateTweet(TweetDTO tweet) {
		if (tweet == null) {
			// do nothing
			return;
		}
		dispatcher.execute(new UpdateTweetAction(tweet), new UpdateTweetHandler());
	}

	@Override
	public void deleteTweet(TweetDTO tweet) {
		if (ctx.getAccount().getAccountId() != tweet.getSender().getAccountId()) {
			homeView.displayMessage(StringConstants.INVALID_ACCESS, AlertType.ERROR);
		}

		dispatcher
				.execute(new DeleteTweetAction(tweet.getTweetId()), new DeleteTweetHandler(tweet));
	}

	@Override
	public void sendTweet(TweetDTO tweet) {
		AccountDTO account = ctx.getAccount();
		if (account == null) {
			placeController.goTo(new LoginPlace());
		}
		dispatcher.execute(new TweetAction(tweet), new TweetHandler());
	}

	@Override
	public void onLogin(String email, String password) {
		mainView.clear();
		dispatcher.execute(new ValidateLoginAction(email, password), new ValidateLoginHandler());
	}

	@Override
	public boolean hasMoreElements() {
		return state.hasMorePages();
	}

	@Override
	public void onScrollBottomHit() {
		if (state.isFetchingData()) {
			return;
		}

		System.out.println("Hit bottom...");
		if (ctx.getAccount() != null && state.hasMorePages()) {
			GetCommunityWallDataAction nextSearchAction = state.getNextSearchAction();
			dispatcher.execute(nextSearchAction, new DispatcherCallbackAsync<GetCommunityWallDataResult>() {
				@Override
				public void onSuccess(GetCommunityWallDataResult result) {
					state.setCurrentTweetList(result.getTweets());
					homeView.addTweets(result.getTweets());
				}
			});
		}
	}

	@Override
	public void displayHashtag(String hashtag) {
		if (hashtag != null) {
			GetCommunityWallDataAction searchCriteriaForHashtag = state.getSearchCriteriaForHashtag(hashtag);
			dispatcher.execute(searchCriteriaForHashtag, new CommunityDataHandler());
		}
	}
	
	@Override
	public void displayMessage(String message, AlertType type) {
		homeView.displayMessage(message, type);
	}

	@Override
	public void reportTweetAsSpam(TweetDTO tweet) {
		SpamDTO spam = new SpamDTO();
		spam.setTweet(tweet);
		spam.setReporter(ctx.getAccount());
		dispatcher.execute(new ReportSpamAction(spam), new ReportSpamActionHandler());
	}

	private void getLatLng(AccountDTO account) {
		GetLatLngAction action = new GetLatLngAction();
		action.setAccount(account);
		dispatcher.execute(action, new DispatcherCallbackAsync<GetLatLngResult>() {

			@Override
			public void onSuccess(GetLatLngResult result) {
				homeView.displayMap(result);
			}
		});
	}

	private TweetViewBinder getDefaultTweetBinder() {
		return new TweetViewBinder(homeView.getTweetSectionElement(), this)
		{
			@Override
			protected boolean detectScrollerHitBottom() {
//				int sh = elem.getScrollHeight();
//				int st = elem.getScrollTop();
//				int of = elem.getOffsetHeight();
//				System.out.println("SH=" + sh + " ST=" + st + " OF=" + of);
				return elem.getScrollHeight() - (elem.getOffsetHeight() + elem.getScrollTop()) < 50;
			}
		};
	}
	
	private void getAccountNotifications() {
		dispatcher.execute(new GetAccountNotificationAction(), accountNotificationHandler);
	}

	// ------------------------------------------------------------------------------------------------------
	//
	// Action Handlers are defined here
	//
	// ------------------------------------------------------------------------------------------------------
	private class TweetHandler extends DispatcherCallbackAsync<TweetResult> {
		@Override
		public void onSuccess(TweetResult result) {
			homeView.insertTweet(result.getTweet());
		}

		@Override
		public void onFailure(Throwable th) {
			if (th instanceof NeedsSubscriptionException) {
				homeView.displayMessage(th.getMessage(), AlertType.ERROR);
			} else if (th instanceof UsageLimitExceededException) {
				homeView.displayMessage(StringConstants.USAGE_LIMIT_EXCEEDED_EXCEPTION,
						AlertType.ERROR);
			} else {
				homeView.displayMessage(StringConstants.INTERNAL_ERROR, AlertType.ERROR);
			}
		}
	}

	private class ValidateLoginHandler extends DispatcherCallbackAsync<ValidateLoginResult> {
		@Override
		public void onSuccess(ValidateLoginResult result) {
			if (result != null && result.getAccount() != null) {
				ctx.setAccount(result.getAccount());
				eventBus.fireEvent(new LoginEvent(result.getAccount()));
				forward(result.getAccount());
			}
		}

		@Override
		public void onFailure(Throwable caught) {
			if (caught instanceof NotFoundException) {
				mainView.displayMessage(LoginWidget.ACCOUNT_DOES_NOT_EXIST, AlertType.ERROR);
			} else if (caught instanceof InvalidCredentialsException) {
				mainView.displayMessage(LoginWidget.INVALID_ACCOUNT_CREDENTIALS, AlertType.ERROR);
			}
			mainView.resetLoginForm();
		}
	}

	private class DeleteTweetHandler extends DispatcherCallbackAsync<DeleteTweetResult> {
		private TweetDTO tweet;

		public DeleteTweetHandler(TweetDTO tweet) {
			this.tweet = tweet;
		}

		@Override
		public void onSuccess(DeleteTweetResult result) {
			homeView.displayMessage(StringConstants.TWEET_REMOVED, AlertType.SUCCESS);
			homeView.removeTweet(tweet);
		}

		@Override
		public void onFailure(Throwable th) {
			if (th instanceof AccessError) {
				homeView.displayMessage(StringConstants.INVALID_ACCESS, AlertType.ERROR);
			} else {
				homeView.displayMessage(StringConstants.INTERNAL_ERROR, AlertType.ERROR);
			}
		}
	};

	private class UpdateTweetHandler extends DispatcherCallbackAsync<UpdateTweetResult> {
		@Override
		public void onSuccess(UpdateTweetResult result) {
			homeView.updateTweet(result.getTweet());
			homeView.displayMessage(StringConstants.TWEET_UPDATED, AlertType.SUCCESS);
		}

		@Override
		public void onFailure(Throwable caught) {
			if (caught instanceof AccessError) {
				homeView.displayMessage(StringConstants.INVALID_ACCESS, AlertType.ERROR);
				return;
			}
			homeView.displayMessage(StringConstants.INTERNAL_ERROR, AlertType.ERROR);
		}
	}

	private class CommunityDataHandler extends DispatcherCallbackAsync<GetCommunityWallDataResult> {
		@Override
		public void onSuccess(GetCommunityWallDataResult result) {
			if (result != null) {
				hideLoadingIcon();
				List<TweetDTO> tweets = result.getTweets();
				state.setCurrentTweetList(tweets);
				state.setFetchingData(false);
				displayTweets(tweets);
				hideLoadingIcon();
				clearBackgroundImage();
			}
		}

		@Override
		public void onFailure(Throwable th) {
			homeView.displayMessage(StringConstants.INTERNAL_ERROR, AlertType.ERROR);
		}
	}

	private class PostCommentHandler extends DispatcherCallbackAsync<CommentResult> {
		private CommentDTO comment;

		public PostCommentHandler(CommentDTO comment) {
			this.comment = comment;
		}

		@Override
		public void onSuccess(CommentResult result) {
			homeView.updateComment(comment);
			homeView.displayMessage(StringConstants.COMMENT_UPDATED, AlertType.SUCCESS);
		}

		@Override
		public void onFailure(Throwable caught) {
			homeView.displayMessage(StringConstants.FAILED_TO_UPDATE_COMMENT, AlertType.SUCCESS);
		}
	};

	public TweetWidget getTweetWidget() {
		return ctx.getTweetWidget();
	}

	private class TweetLikeActionHandler extends DispatcherCallbackAsync<LikeResult> {
		@Override
		public void onSuccess(LikeResult result) {
			homeView.updateTweetLike(result.getLike());
			homeView.displayMessage(StringConstants.LIKE_SAVED, AlertType.SUCCESS);
		}

		@Override
		public void onFailure(Throwable caught) {
			if (caught instanceof DuplicateException) {
				homeView.displayMessage(StringConstants.FAILED_TO_SAVE_LIKE, AlertType.ERROR);
			}
		}
	}
	
	private class ReportSpamActionHandler extends DispatcherCallbackAsync<ReportSpamResult> {
		@Override
		public void onSuccess(ReportSpamResult result) {
			homeView.displayMessage(StringConstants.REPORT_SPAM_SUCCESSFUL, AlertType.SUCCESS);
		}
	}

	@Override
	public void sendMessage(ConversationDTO conversation) {
		if (conversation == null) {
			throw new IllegalArgumentException();
		}
		
		// make sure user is logged in
		if (ctx.getAccount() == null) {
//			homeView.closeMessageWidget();
			goTo(new LoginPlace());
			return;
		}
		
		// TODO check size
		int size = conversation.getMessages().size();
		conversation.getMessages().get(size-1).setSender(ctx.getAccount());
		conversation.setSender(ctx.getAccount());
		dispatcher.execute(new SendMessageAction(conversation), new DispatcherCallbackAsync<SendMessageResult>() {
			@Override
			public void onSuccess(SendMessageResult result) {
				homeView.displayMessage(StringConstants.MESSAGE_SENT, AlertType.SUCCESS);
			}
			
			@Override
			public void onFailure(Throwable th) {
				homeView.displayMessage(StringConstants.MESSAGE_NOT_DELIVERED, AlertType.ERROR);
			}
		});
	};
	
	private class GetLoggedInUserActionHandler extends DispatcherCallbackAsync<GetLoggedInUserResult> {
		@Override
		public void onSuccess(GetLoggedInUserResult result) {
			if (result != null && result.getAccount() != null) {
				ctx.setAccount(result.getAccount());
				eventBus.fireEvent(new LoginEvent(result.getAccount()));
				displayCommunityWall();
				getAccountNotifications();
			} else {
				displayMainView();
			}
			hideLoadingIcon();
		}
	}

	private class GetFacebookRedirectUriHandler extends DispatcherCallbackAsync<GetFacebookRedirectUriResult> {
		@Override
		public void onSuccess(GetFacebookRedirectUriResult result) {
			if (result != null) {
				mainView.setRedirectUri(result.getRedirectUrl());
			}
		}
	}
	
	private class GetNeighborhoodDetailsHandler extends DispatcherCallbackAsync<GetNeighborhoodDetailsResult> {
		@Override
		public void onSuccess(GetNeighborhoodDetailsResult result) {
			homeView.displayResidentCount(result.getTotalResidents());
		}
	}
}
