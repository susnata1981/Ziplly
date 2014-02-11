package com.ziplly.app.client.activities;

import java.util.List;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.exceptions.AccessError;
import com.ziplly.app.client.exceptions.DuplicateException;
import com.ziplly.app.client.exceptions.NeedsSubscriptionException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.exceptions.UsageLimitExceededException;
import com.ziplly.app.client.places.BusinessPlace;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.places.ResidentPlace;
import com.ziplly.app.client.places.SignupPlace;
import com.ziplly.app.client.view.HomeView;
import com.ziplly.app.client.view.HomeView.HomePresenter;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.View;
import com.ziplly.app.client.view.event.AccountDetailsUpdateEvent;
import com.ziplly.app.client.view.event.LoadingEventEnd;
import com.ziplly.app.client.view.event.LoadingEventStart;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.view.handler.AccountDetailsUpdateEventHandler;
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
import com.ziplly.app.shared.DeleteImageAction;
import com.ziplly.app.shared.DeleteImageResult;
import com.ziplly.app.shared.DeleteTweetAction;
import com.ziplly.app.shared.DeleteTweetResult;
import com.ziplly.app.shared.EmailAdminAction;
import com.ziplly.app.shared.EmailAdminResult;
import com.ziplly.app.shared.GetAccountDetailsAction;
import com.ziplly.app.shared.GetAccountDetailsResult;
import com.ziplly.app.shared.GetAccountNotificationAction;
import com.ziplly.app.shared.GetCommunityWallDataAction;
import com.ziplly.app.shared.GetCommunityWallDataResult;
import com.ziplly.app.shared.GetHashtagAction;
import com.ziplly.app.shared.GetHashtagResult;
import com.ziplly.app.shared.GetImageUploadUrlAction;
import com.ziplly.app.shared.GetImageUploadUrlResult;
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
import com.ziplly.app.shared.UpdateCommentAction;
import com.ziplly.app.shared.UpdateCommentResult;
import com.ziplly.app.shared.UpdateTweetAction;
import com.ziplly.app.shared.UpdateTweetResult;

public class HomeActivity extends AbstractActivity implements HomePresenter, InfiniteScrollHandler {
	private IHomeView homeView;
	private HomeViewState state;
	private TweetViewBinder binder;
	private HomePlace place;
	private AccountDTO account;
	private AcceptsOneWidget panel;

	private AccountNotificationHandler accountNotificationHandler = new AccountNotificationHandler();
	private CommunityDataHandler communityDataHandler = new CommunityDataHandler();
	private GetLoggedInUserActionHandler getLoggedInUserActionHandler = new GetLoggedInUserActionHandler();
	private GetNeighborhoodDetailsHandler neighborhoodDetailsHandler = new GetNeighborhoodDetailsHandler();
	
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

		void highlightTweetType(TweetType type);

		void setUnreadMessageCount(Long count);

		void setImageUploadUrl(String imageUrl);

		void addUploadFormHandler(SubmitCompleteHandler submitCompleteHandler);

		void displayProfileImagePreview(String imageUrl);

		void resetImageUploadUrl();

		void displayCommunitySummaryDetails(GetNeighborhoodDetailsResult result);

		void addComment(CommentDTO comment);

		void displayTargetNeighborhoods(List<NeighborhoodDTO> targetNeighborhoodList);
	}

	@Inject
	public HomeActivity(CachingDispatcherAsync dispatcher, EventBus eventBus, HomePlace place,
			PlaceController placeController,ApplicationContext ctx, 
			HomeView homeView) {

		super(dispatcher, eventBus, placeController, ctx);
		this.place = place;
		this.homeView = homeView;
		state = new HomeViewState();
		setupHandlers();
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		bind();
		if (ctx.getAccount() != null) {
			state.setCurrentNeighborhood(ctx.getAccount().getNeighborhood());
			displayCommunityWall();
		} else {
			fetchData();
		}
	}

	@Override
	protected void setupHandlers() {
		super.setupHandlers();
		eventBus.addHandler(AccountDetailsUpdateEvent.TYPE, new AccountDetailsUpdateEventHandler() {
			
			@Override
			public void onEvent(AccountDetailsUpdateEvent event) {
				ctx.updateAccountDetails(event.getAccountDetails());
				homeView.setUnreadMessageCount(new Long(ctx.getUnreadMessageCount()));
			}
		});
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
		getCommunityWallData(state.getSearchCriteria(place));
		getHashtagList();
		getCountsForTweetTypes(state.getCurrentNeighborhood().getNeighborhoodId());
		getNeighborhoodDetails();
		
		// account specific.
		getLatLng(ctx.getAccount());
		getAccountNotifications();
		setImageUploadUrl();
		setUploadImageHandler();
		getAccountDetails();
		homeView.displayTargetNeighborhoods(getTargetNeighborhoodList());
		displayHomeView();
	}

	@Override
	public void displayCommunityWallForNeighborhood(NeighborhoodDTO neighborhood) {
		GetCommunityWallDataAction searchCriteria = state.getSearchCriteria(neighborhood);
		getCommunityWallData(searchCriteria);
		getHashtagList();
		getCountsForTweetTypes(state.getCurrentNeighborhood().getNeighborhoodId());
		getNeighborhoodDetails();
	}
	
	private void getNeighborhoodDetails() {
		GetNeighborhoodDetailsAction action = new GetNeighborhoodDetailsAction(
				state.getCurrentNeighborhood().getNeighborhoodId());
		dispatcher.execute(action, neighborhoodDetailsHandler);
		homeView.displaySummaryData(state.getCurrentNeighborhood());
	}

	private void getCountsForTweetTypes(Long neighborhoodId) {
		dispatcher.execute(new GetTweetCategoryDetailsAction(neighborhoodId), 
				new DispatcherCallbackAsync<GetTweetCategoryDetailsResult>() {
			@Override
			public void onSuccess(GetTweetCategoryDetailsResult result) {
				// do something.
				homeView.updateTweetCategoryCount(result.getTweetCounts());
			}
		});
	}

	private void getHashtagList() {
		GetHashtagAction action = new GetHashtagAction(state.getCurrentNeighborhood().getNeighborhoodId());
		dispatcher.execute(action, new DispatcherCallbackAsync<GetHashtagResult>() {
			@Override
			public void onSuccess(GetHashtagResult result) {
				homeView.displayHashtag(result.getHashtags());
			}
		});
	}

	private void getAccountDetails() {
		dispatcher.execute(new GetAccountDetailsAction(), new DispatcherCallbackAsync<GetAccountDetailsResult>() {

			@Override
			public void onSuccess(GetAccountDetailsResult result) {
				eventBus.fireEvent(new AccountDetailsUpdateEvent(result));
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
		homeView.clear();
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
		homeView.setPresenter(this);
	}

	void displayHomeView() {
		hideLoadingIcon();
		panel.setWidget(homeView);
	}

	@Override
	public void fetchData() {
		dispatcher.execute(new GetLoggedInUserAction(), getLoggedInUserActionHandler);
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

		dispatcher.execute(new DeleteTweetAction(tweet.getTweetId()), new DeleteTweetHandler(tweet));
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
	public boolean hasMoreElements() {
		return state.hasMorePages();
	}

	@Override
	public void onScrollBottomHit() {
		if (state.isFetchingData()) {
			return;
		}

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
			eventBus.fireEvent(new LoadingEventStart());
			GetCommunityWallDataAction searchCriteriaForHashtag = state.getSearchCriteriaForHashtag(hashtag);
			dispatcher.execute(searchCriteriaForHashtag, communityDataHandler);
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

	
	public void setImageUploadUrl() {
		dispatcher.execute(new GetImageUploadUrlAction(),
				new DispatcherCallbackAsync<GetImageUploadUrlResult>() {
					@Override
					public void onSuccess(GetImageUploadUrlResult result) {
						homeView.setImageUploadUrl(result.getImageUrl());
					}
				});
	}

	// TODO handle image deletion on multiple file uploads
	public void setUploadImageHandler() {
		homeView.addUploadFormHandler(new FormPanel.SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				String imageUrl = event.getResults();
				homeView.displayProfileImagePreview(imageUrl);
				homeView.resetImageUploadUrl();
				setImageUploadUrl();
			}
		});
	}
	
	/**
	 * Deletes the image from blobstore based on serving url
	 */
	@Override
	public void deleteImage(String url) {
		dispatcher.execute(new DeleteImageAction(url), new DispatcherCallbackAsync<DeleteImageResult>() {
			@Override
			public void onSuccess(DeleteImageResult result) {
				// Nothing to do.
			}
		});
	}
	
	@Override
	public void updateComment(CommentDTO comment) {
		dispatcher.execute(new UpdateCommentAction(comment), new DispatcherCallbackAsync<UpdateCommentResult>() {
			@Override
			public void onSuccess(UpdateCommentResult result) {
				homeView.displayMessage(StringConstants.COMMENT_UPDATED, AlertType.SUCCESS);
				homeView.updateComment(result.getComment());
			}
			
			@Override
			public void onFailure(Throwable th) {
				if (th instanceof AccessError) {
					homeView.displayMessage(StringConstants.INVALID_ACCESS, AlertType.ERROR);
				} else {
					homeView.displayMessage(StringConstants.INTERNAL_ERROR, AlertType.ERROR);
				}
			}
		});
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
			eventBus.fireEvent(new LoadingEventEnd());
		}

		@Override
		public void onFailure(Throwable th) {
			if (th instanceof IllegalArgumentException) {
				homeView.displayMessage(StringConstants.INVALID_URL, AlertType.ERROR);
			} else if (th instanceof NotFoundException) {
				getCommunityWallData(TweetType.ALL);
				homeView.displayMessage(StringConstants.INVALID_URL, AlertType.ERROR);
			}
			eventBus.fireEvent(new LoadingEventEnd());
		}
	}

	private class PostCommentHandler extends DispatcherCallbackAsync<CommentResult> {
		private CommentDTO comment;

		public PostCommentHandler(CommentDTO comment) {
			this.comment = comment;
		}

		@Override
		public void onSuccess(CommentResult result) {
			homeView.addComment(comment);
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
				state.setCurrentNeighborhood(result.getAccount().getNeighborhood());
				eventBus.fireEvent(new LoginEvent(result.getAccount()));
				displayCommunityWall();
			} else {
				goTo(new SignupPlace());
			}
		}
	}
	
	private class GetNeighborhoodDetailsHandler extends DispatcherCallbackAsync<GetNeighborhoodDetailsResult> {
		@Override
		public void onSuccess(GetNeighborhoodDetailsResult result) {
			System.out.println("Total business count: "+result.getTotalBusinesses());
			homeView.displayCommunitySummaryDetails(result);
		}
		
		@Override
		public void onFailure(Throwable th) {
			System.out.println("Resutl: "+th.getMessage());
		}
	}

	@Override
	public void sendFeedback(String content) {
		EmailAdminAction action = new EmailAdminAction();
		action.setContent(content);
		action.setSubject(StringConstants.FEEDBACK+" from "+ctx.getAccount().getEmail());
		action.setFrom(ctx.getAccount().getEmail());
		dispatcher.execute(action, new DispatcherCallbackAsync<EmailAdminResult>() {
			@Override
			public void onSuccess(EmailAdminResult result) {
				homeView.displayMessage(StringConstants.FEEDBACK_SENT_SUCCESS, AlertType.SUCCESS);	
			}
			
			@Override
			public void onFailure(Throwable th) {
				homeView.displayMessage(StringConstants.FEEDBACK_SENT_FAILURE, AlertType.ERROR);
			}
		});
	}

	@Override
	public void gotoResidentPlace() {
		ResidentPlace place = new ResidentPlace();
		place.setNeighborhoodId(state.getCurrentNeighborhood().getNeighborhoodId());
		placeController.goTo(place);
	}

	@Override
	public void gotoBusinessPlace() {
		BusinessPlace place = new BusinessPlace();
		place.setNeighborhoodId(state.getCurrentNeighborhood().getNeighborhoodId());
		placeController.goTo(place);
	}
	
	
	void getCommunityWallData(TweetType type) {
		eventBus.fireEvent(new LoadingEventStart());
		GetCommunityWallDataAction searchCriteria = state.getSearchCriteriaForTweetType(type);
		state.setFetchingData(true);
		dispatcher.execute(searchCriteria, communityDataHandler);
		startViewBinder();
	}

	void getCommunityWallData(GetCommunityWallDataAction action) {
		// Load first batch of data
		eventBus.fireEvent(new LoadingEventStart());
		state.setFetchingData(true);
		dispatcher.execute(action, communityDataHandler);
		startViewBinder();
	}
	
	private void startViewBinder() {
		if (binder != null) {
			binder.stop();
		}
		
		binder = new TweetViewBinder(homeView.getTweetSectionElement(), this);//getDefaultTweetBinder();
		binder.start();
	}
}

