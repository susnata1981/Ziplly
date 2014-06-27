package com.ziplly.app.client.activities;

import java.util.List;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.places.BusinessPlace;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.places.ResidentPlace;
import com.ziplly.app.client.places.SignupPlace;
import com.ziplly.app.client.view.HomeViewImpl;
import com.ziplly.app.client.view.HomeViewImpl.HomePresenter;
import com.ziplly.app.client.view.ITweetView;
import com.ziplly.app.client.view.ImageUtil;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.View;
import com.ziplly.app.client.view.coupon.CouponFormWidget;
import com.ziplly.app.client.view.event.AccountDetailsUpdateEvent;
import com.ziplly.app.client.view.event.LoadingEventEnd;
import com.ziplly.app.client.view.event.LoadingEventStart;
import com.ziplly.app.client.view.handler.AccountDetailsUpdateEventHandler;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.client.widget.TweetBox;
import com.ziplly.app.client.widget.TweetWidget;
import com.ziplly.app.client.widget.blocks.FormUploadWidget;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.model.EntityType;
import com.ziplly.app.model.HashtagDTO;
import com.ziplly.app.model.ImageDTO;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.SpamDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.shared.CancelCouponPurchaseAction;
import com.ziplly.app.shared.CancelCouponPurchaseResult;
import com.ziplly.app.shared.CheckBuyerEligibilityForCouponAction;
import com.ziplly.app.shared.CheckBuyerEligibilityForCouponResult;
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
import com.ziplly.app.shared.GetNeighborhoodDetailsAction;
import com.ziplly.app.shared.GetNeighborhoodDetailsResult;
import com.ziplly.app.shared.GetNewMemberAction;
import com.ziplly.app.shared.GetNewMemberResult;
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

public class HomeActivity extends AbstractActivity implements HomePresenter, TweetPresenter, InfiniteScrollHandler {
	
	private static final int LOOKBACK_DAYS = 30;
	private static final int MAX_HASHTAG_COUNT = 5;
	
	private AccountNotificationHandler accountNotificationHandler = new AccountNotificationHandler();
	
	private TweetViewBinder binder;
	
	private CommunityDataHandler communityDataHandler = new CommunityDataHandler();
	
	private GetNeighborhoodDetailsHandler neighborhoodDetailsHandler =
	    new GetNeighborhoodDetailsHandler();
	private AcceptsOneWidget panel;
	
	private HomePlace place;

	private HomeViewState state;
	
	private HomeView view;
	private AsyncProvider<HomeViewImpl> viewProvider;

	@Inject
	public HomeActivity(CachingDispatcherAsync dispatcher,
	    EventBus eventBus,
	    HomePlace place,
	    PlaceController placeController,
	    ApplicationContext ctx,
	    AsyncProvider<HomeViewImpl> viewProvider) {

		super(dispatcher, eventBus, placeController, ctx);
		this.place = place;
		this.viewProvider = viewProvider;
		state = new HomeViewState();
	}

	@Override
	public void bind() {
		view.setPresenter(this);
		view.getTweetView().setPresenter(this);
		view.getTweetWidget().setPresenter(this);
	}

	@Override
	public void checkCouponPurchaseEligibility(final CouponDTO coupon, final TweetWidget widget) {
		CheckBuyerEligibilityForCouponAction eligibilityAction =
		    new CheckBuyerEligibilityForCouponAction();
		eligibilityAction.setCoupon(coupon);
		dispatcher.execute(
		    eligibilityAction,
		    new DispatcherCallbackAsync<CheckBuyerEligibilityForCouponResult>() {

			    /* (non-Javadoc)
			     * @see com.google.gwt.user.client.rpc.AsyncCallback#onSuccess(java.lang.Object)
			     */
			    @Override
			    public void onSuccess(CheckBuyerEligibilityForCouponResult result) {
				    widget.initiatePay(result.getJwtToken());
			    }
		    });
	}

	/**
	 * Deletes the image from blobstore based on serving url
	 */
	@Override
	public void deleteImage(String url) {
		dispatcher.execute(
		    new DeleteImageAction(url),
		    new DispatcherCallbackAsync<DeleteImageResult>() {
			    @Override
			    public void onSuccess(DeleteImageResult result) {
				    // Nothing to do.
			    }
		    });
	}

	@Override
	public void deleteTweet(TweetDTO tweet) {
		if (ctx.getAccount().getAccountId() != tweet.getSender().getAccountId()) {
			view.displayMessage(StringConstants.INVALID_ACCESS, AlertType.ERROR);
		}

		dispatcher.execute(new DeleteTweetAction(tweet.getTweetId()), new DeleteTweetHandler(tweet));
	}

	@Override
	public void displayCommunityWallForNeighborhood(NeighborhoodDTO neighborhood) {
		GetCommunityWallDataAction searchCriteria = state.getSearchCriteria(neighborhood);
		getCommunityWallData(searchCriteria);
		getHashtagList();
		getCountsForTweetTypes(state.getCurrentNeighborhood().getNeighborhoodId());
		getNeighborhoodDetails();
	}

	@Override
	public void displayHashtag(String hashtag) {
		if (hashtag != null) {
			eventBus.fireEvent(new LoadingEventStart());
			GetCommunityWallDataAction searchCriteriaForHashtag =
			    state.getSearchCriteriaForHashtag(hashtag);
			dispatcher.execute(searchCriteriaForHashtag, communityDataHandler);
		}
	}

	@Override
	public void displayMessage(String message, AlertType type) {
		view.displayMessage(message, type);
	}

	@Override
	public void displayPublicProfile(Long accountId) {
		placeController.goTo(new LoginPlace());
	}

	@Override
	public void displayTweets(List<TweetDTO> tweets) {
		view.display(tweets);
		panel.setWidget(view);
	}

	@Override
	public void getCouponFormActionUrl(CouponFormWidget couponFormWidget) {
	}

	@Override
	public void go(AcceptsOneWidget container) {
	}

	@Override
	public void gotoBusinessPlace() {
		BusinessPlace place = new BusinessPlace();
		place.setNeighborhoodId(state.getCurrentNeighborhood().getNeighborhoodId());
		placeController.goTo(place);
	}

	@Override
	public void gotoResidentPlace() {
		ResidentPlace place = new ResidentPlace();
		place.setNeighborhoodId(state.getCurrentNeighborhood().getNeighborhoodId());
		placeController.goTo(place);
	}

	@Override
	public boolean hasMoreElements() {
		return state.hasMorePages();
	}

	@Override
	public void initializeUploadForm(final FormUploadWidget formUploadWidget) {
		setFormUploadActionUrl(formUploadWidget);
		
		formUploadWidget.setUploadFormSubmitCompleteHandler(new SubmitCompleteHandler() {

			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				if (event.getResults() != null) {
					ImageDTO imageDto = ImageUtil.parseImageUrl(event.getResults());
					formUploadWidget.displayImagePreview(imageDto.getUrl());
					setFormUploadActionUrl(formUploadWidget);
				}
			}

		});
	}

	@Override
	public void likeTweet(Long tweetId) {
		LikeTweetAction action = new LikeTweetAction();
		action.setTweetId(tweetId);
		dispatcher.execute(action, new TweetLikeActionHandler());
	}

	/**
	 * Loads the messages for the next page.
	 */
	@Override
	public void onScrollBottomHit() {
		if (state.isFetchingData()) {
			return;
		}

		if (ctx.getAccount() != null && state.hasMorePages()) {
			GetCommunityWallDataAction nextSearchAction = state.getNextSearchAction();
			dispatcher.execute(
			    nextSearchAction,
			    new DispatcherCallbackAsync<GetCommunityWallDataResult>() {
				    @Override
				    public void onSuccess(GetCommunityWallDataResult result) {
					    state.setCurrentTweetList(result.getTweets());
					    view.addTweets(result.getTweets());
				    }
			    });
		}
	}

	@Override
	public void onStop() {
		StyleHelper.clearBackground();
		if (binder != null) {
			binder.stop();
		}
		hideLoadingIcon();
		view.clear();
	}

	@Override
	public void postComment(final CommentDTO comment) {
		dispatcher.execute(new CommentAction(comment), new PostCommentHandler());
	}

	@Override
	public void reportTweetAsSpam(TweetDTO tweet) {
		SpamDTO spam = new SpamDTO();
		spam.setTweet(tweet);
		spam.setReporter(ctx.getAccount());
		dispatcher.execute(new ReportSpamAction(spam), new ReportSpamActionHandler());
	}

	@Override
	public void sendFeedback(String content) {
		EmailAdminAction action = new EmailAdminAction();
		action.setContent(content);
		action.setSubject(StringConstants.FEEDBACK + " from " + ctx.getAccount().getEmail());
		action.setFrom(ctx.getAccount().getEmail());
		dispatcher.execute(action, new DispatcherCallbackAsync<EmailAdminResult>() {

			@Override
			public void onSuccess(EmailAdminResult result) {
				view.displayMessage(StringConstants.FEEDBACK_SENT_SUCCESS, AlertType.SUCCESS);
			}
		});
	}

	@Override
	public void sendMessage(ConversationDTO conversation) {
		if (conversation == null) {
			throw new IllegalArgumentException();
		}

		// make sure user is logged in
		if (ctx.getAccount() == null) {
			// homeView.closeMessageWidget();
			goTo(new LoginPlace());
			return;
		}

		int size = conversation.getMessages().size();
		conversation.getMessages().get(size - 1).setSender(ctx.getAccount());
		conversation.setSender(ctx.getAccount());
		dispatcher.execute(
		    new SendMessageAction(conversation),
		    new DispatcherCallbackAsync<SendMessageResult>() {

			    @Override
			    public void onSuccess(SendMessageResult result) {
				    view.displayMessage(StringConstants.MESSAGE_SENT, AlertType.SUCCESS);
			    }
		    });
	}

	@Override
	public void sendTweet(TweetDTO tweet) {
		AccountDTO account = ctx.getAccount();
		if (account == null) {
			placeController.goTo(new LoginPlace());
		}
		
		dispatcher.execute(new TweetAction(tweet), new TweetHandler());
	}

	public void setImageUploadUrl() {
		dispatcher.execute(
		    new GetImageUploadUrlAction(),
		    new DispatcherCallbackAsync<GetImageUploadUrlResult>() {
			    @Override
			    public void onSuccess(GetImageUploadUrlResult result) {
				    view.setImageUploadUrl(result.getImageUrl());
			    }
		    });
	}

	// TODO handle image deletion on multiple file uploads
	public void setUploadImageHandler() {
		view.addUploadFormHandler(new FormPanel.SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				try {
					String imageUrl = event.getResults();
					view.displayProfileImagePreview(imageUrl);
				} finally {
					view.resetImageUploadUrl();
					setImageUploadUrl();
				}
			}
		});
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		checkAccountLogin();
	}

	@Override
	public void updateComment(CommentDTO comment) {
		dispatcher.execute(
		    new UpdateCommentAction(comment),
		    new DispatcherCallbackAsync<UpdateCommentResult>() {

			    @Override
			    public void onSuccess(UpdateCommentResult result) {
				    view.displayMessage(StringConstants.COMMENT_UPDATED, AlertType.SUCCESS);
				    view.updateComment(result.getComment());
			    }
		    });
	}

	@Override
	public void updateTweet(TweetDTO tweet) {
		if (tweet == null) {
			// do nothing
			return;
		}
		dispatcher.execute(new UpdateTweetAction(tweet), new UpdateTweetHandler(tweet));
	}

	@Override
	protected void doStart() {
		state.setCurrentNeighborhood(ctx.getCurrentNeighborhood());
		displayCommunityWall();
		// account specific.
		getAccountNotifications();
		getAccountDetails();
	}

	@Override
	protected void doStartOnUserNotLoggedIn() {
		if (place.getTweetId() != null) {
			GetCommunityWallDataAction searchCriteria = state.getSearchCriteria(place);
			getTweetData(searchCriteria);
			return;
		}

		placeController.goTo(new SignupPlace());
	}

	@Override
	protected void setupHandlers() {
		super.setupHandlers();
		eventBus.addHandler(AccountDetailsUpdateEvent.TYPE, new AccountDetailsUpdateEventHandler() {

			@Override
			public void onEvent(AccountDetailsUpdateEvent event) {
				ctx.setAccountDetails(event.getAccountDetails());
				view.setUnreadMessageCount(new Long(ctx.getUnreadMessageCount()));
			}
		});
	}

	void displayHomeView() {
		hideLoadingIcon();
		panel.setWidget(view);
		view.displayNeighborhoodImage(ctx.getCurrentNeighborhood());
	}

	void getCommunityWallData(GetCommunityWallDataAction action) {
		// Load first batch of data
		eventBus.fireEvent(new LoadingEventStart());
		state.setFetchingData(true);
		dispatcher.execute(action, communityDataHandler);
		startViewBinder();
	}

	void getCommunityWallData(TweetType type) {
		eventBus.fireEvent(new LoadingEventStart());
		GetCommunityWallDataAction searchCriteria = state.getSearchCriteriaForTweetType(type);
		state.setFetchingData(true);
		dispatcher.execute(searchCriteria, communityDataHandler);
		startViewBinder();
	}
	
	void getTweetData(GetCommunityWallDataAction action) {
		// Load first batch of data
		eventBus.fireEvent(new LoadingEventStart());
		state.setFetchingData(true);
		dispatcher.execute(action, communityDataHandler);
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
		viewProvider.get(new AsyncCallback<HomeViewImpl>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to load information.");
			}

			@Override
			public void onSuccess(HomeViewImpl result) {
				HomeActivity.this.view = result;
				bind();
				setupHandlers();
				setImageUploadUrl();
				setUploadImageHandler();
				getCommunityWallData(state.getSearchCriteria(place));
				getHashtagList();
				displayMap(ctx.getCurrentNeighborhood());
				getCountsForTweetTypes(state.getCurrentNeighborhood().getNeighborhoodId());
				getNeighborhoodDetails();
				view.displayTargetNeighborhoods(getTargetNeighborhoodList());
				displayHomeView();
				eventBus.fireEvent(new LoadingEventStart());
				displayNewMemberList();
			}
		});
	}

	private void displayMap(NeighborhoodDTO n) {
		view.displayMap(n.getPostalCodes().get(0).toString());
	}

	private void displayNewMemberList() {
		GetNewMemberAction action = new GetNewMemberAction();
		action.setDaysLookback(LOOKBACK_DAYS);
		action.setEntityType(EntityType.PERSONAL_ACCOUNT);
		action.setNeighborhoodId(ctx.getCurrentNeighborhood().getNeighborhoodId());

		dispatcher.execute(action, new DispatcherCallbackAsync<GetNewMemberResult>() {

			@Override
			public void onSuccess(GetNewMemberResult result) {
				view.displayNewMembers(result.getAccounts());
				view.resizeMap();
			}
		});
	};

	private void getAccountDetails() {
		dispatcher.execute(
		    new GetAccountDetailsAction(),
		    new DispatcherCallbackAsync<GetAccountDetailsResult>() {

			    @Override
			    public void onSuccess(GetAccountDetailsResult result) {
				    eventBus.fireEvent(new AccountDetailsUpdateEvent(result));
			    }
			    
//			    public void onFailure(Throwable th) {
//				    //homeView.displayMessage(StringConstants.FAILED_TO_BUY_COUPON, AlertType.ERROR);
//			    	Window.alert(th.getLocalizedMessage());
//			    }
		    });
	}

	private void getAccountNotifications() {
		dispatcher.execute(new GetAccountNotificationAction(), accountNotificationHandler);
	}

	private void getCountsForTweetTypes(Long neighborhoodId) {
		dispatcher.execute(
		    new GetTweetCategoryDetailsAction(neighborhoodId),
		    new DispatcherCallbackAsync<GetTweetCategoryDetailsResult>() {
			    @Override
			    public void onSuccess(GetTweetCategoryDetailsResult result) {
				    // do something.
				    view.updateTweetCategoryCount(result.getTweetCounts());
			    }
		    });
	};

	private void getHashtagList() {
		GetHashtagAction action =
		    new GetHashtagAction(state.getCurrentNeighborhood().getNeighborhoodId());
		action.setSize(MAX_HASHTAG_COUNT);
		dispatcher.execute(action, new DispatcherCallbackAsync<GetHashtagResult>() {
			@Override
			public void onSuccess(GetHashtagResult result) {
				view.displayHashtag(result.getHashtags());
			}
		});
	}

	private void getNeighborhoodDetails() {
		GetNeighborhoodDetailsAction action =
		    new GetNeighborhoodDetailsAction(state.getCurrentNeighborhood().getNeighborhoodId());
		dispatcher.execute(action, neighborhoodDetailsHandler);
		view.displaySummaryData(state.getCurrentNeighborhood());
	}

	private void setFormUploadActionUrl(final FormUploadWidget formUploadWidget) {
		dispatcher.execute(
		    new GetImageUploadUrlAction(),
		    new DispatcherCallbackAsync<GetImageUploadUrlResult>() {

			    @Override
			    public void onSuccess(GetImageUploadUrlResult result) {
				    if (result.getImageUrl() != null) {
					    formUploadWidget.setUploadFormActionUrl(result.getImageUrl());
					    formUploadWidget.enableUploadButton();
				    }
			    }
		    });
	}

	private void startViewBinder() {
		if (binder != null) {
			binder.stop();
		}

		binder = new TweetViewBinder(view.getTweetSectionElement(), this);// getDefaultTweetBinder();
		binder.start();
	};

	public static interface HomeView extends View<HomePresenter> {
		void addComment(CommentDTO comment);

		void addTweet(TweetDTO tweet);

		void addTweets(List<TweetDTO> tweets);

		void addUploadFormHandler(SubmitCompleteHandler submitCompleteHandler);

		void display(List<TweetDTO> tweets);

		void displayCommunitySummaryDetails(GetNeighborhoodDetailsResult result);

		void displayHashtag(List<HashtagDTO> hashtags);

		void displayMap(String address);

		void displayMessage(String message, AlertType error);

		void displayNeighborhoodImage(NeighborhoodDTO neighborhood);

		void displayNewMembers(List<AccountDTO> accounts);

		void displayProfileImagePreview(String imageUrl);

		void displaySummaryData(NeighborhoodDTO neighborhood);

		void displayTargetNeighborhoods(List<NeighborhoodDTO> targetNeighborhoodList);

		Element getTweetSectionElement();

		ITweetView<TweetPresenter> getTweetView();

		TweetBox getTweetWidget();

		void highlightTweetType(TweetType type);

		void insertTweet(TweetDTO tweet);

		void removeTweet(TweetDTO tweet);

		void resetImageUploadUrl();

		void resizeMap();

		void setImageUploadUrl(String imageUrl);

		void setUnreadMessageCount(Long count);

		void updateComment(CommentDTO comment);

		void updateTweet(TweetDTO tweet);

		void updateTweetCategoryCount(Map<TweetType, Integer> tweetCounts);

		void updateTweetLike(LoveDTO like);

		void updateTweets(List<TweetDTO> tweets);

		void refreshTweet(TweetDTO tweet);
	}

	private class CommunityDataHandler extends DispatcherCallbackAsync<GetCommunityWallDataResult> {

		public void postHandle(Throwable th) {
			if (th instanceof NotFoundException) {
				getCommunityWallData(TweetType.ALL);
			}
			eventBus.fireEvent(new LoadingEventEnd());
		}
		
		@Override
		public void onSuccess(GetCommunityWallDataResult result) {
			if (result != null) {
				List<TweetDTO> tweets = result.getTweets();
				state.setCurrentTweetList(tweets);
				state.setFetchingData(false);
				displayTweets(tweets);
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
			view.displayMessage(StringConstants.TWEET_REMOVED, AlertType.SUCCESS);
			view.removeTweet(tweet);
		}
	}

	private class GetNeighborhoodDetailsHandler extends
	    DispatcherCallbackAsync<GetNeighborhoodDetailsResult> {

		@Override
		public void onSuccess(GetNeighborhoodDetailsResult result) {
			view.displayCommunitySummaryDetails(result);
		}
	}

	private class PostCommentHandler extends DispatcherCallbackAsync<CommentResult> {
		@Override
		public void onSuccess(CommentResult result) {
			view.addComment(result.getComment());
			view.displayMessage(StringConstants.COMMENT_UPDATED, AlertType.SUCCESS);
		}
	}

	private class ReportSpamActionHandler extends DispatcherCallbackAsync<ReportSpamResult> {
		@Override
		public void onSuccess(ReportSpamResult result) {
			view.displayMessage(StringConstants.REPORT_SPAM_SUCCESSFUL, AlertType.SUCCESS);
		}
	}

	private class TweetHandler extends DispatcherCallbackAsync<TweetResult> {
	  public TweetHandler() {
	    super(eventBus);
    }
	  
		@Override
		public void onSuccess(TweetResult result) {
			view.insertTweet(result.getTweet());
		}
	}

	private class TweetLikeActionHandler extends DispatcherCallbackAsync<LikeResult> {

		@Override
		public void onSuccess(LikeResult result) {
			view.updateTweetLike(result.getLike());
			view.displayMessage(stringDefinitions.likeSaved(), AlertType.SUCCESS);
		}
	}

	private class UpdateTweetHandler extends DispatcherCallbackAsync<UpdateTweetResult> {
		private TweetDTO tweet;

		public UpdateTweetHandler(TweetDTO tweet) {
			this.tweet = tweet;
		}

		@Override
		public void onAccessError() {
			HomeActivity.this.view.refreshTweet(tweet);
		}
		
		@Override
		public void onSuccess(UpdateTweetResult result) {
			view.updateTweet(result.getTweet());
			view.displayMessage(StringConstants.TWEET_UPDATED, AlertType.SUCCESS);
		}
	}

  @Override
  public void cancelTransaction(long purchaseCouponId) {
    CancelCouponPurchaseAction action = new CancelCouponPurchaseAction();
    action.setPurchaseCouponId(purchaseCouponId);
    dispatcher.execute(action, new DispatcherCallbackAsync<CancelCouponPurchaseResult>() {

      @Override
      public void onSuccess(CancelCouponPurchaseResult result) {
        view.displayMessage(stringDefinitions.paymentCanclled(), AlertType.WARNING);
      }
    });
  }
}
