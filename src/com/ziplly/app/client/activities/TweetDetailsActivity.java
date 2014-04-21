package com.ziplly.app.client.activities;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.places.SignupPlace;
import com.ziplly.app.client.places.TweetDetailsPlace;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.TweetDetailsView;
import com.ziplly.app.client.widget.CouponFormWidget;
import com.ziplly.app.client.widget.TweetWidget;
import com.ziplly.app.client.widget.blocks.FormUploadWidget;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.overlay.GoogleWalletSuccessResult;
import com.ziplly.app.shared.GetCommunityWallDataAction;
import com.ziplly.app.shared.GetCommunityWallDataAction.SearchType;
import com.ziplly.app.shared.GetCommunityWallDataResult;

public class TweetDetailsActivity extends AbstractActivity implements TweetPresenter {
	private TweetDetailsPlace place;
	private TweetDetailsView view;
	private AsyncProvider<TweetDetailsView> viewProvider;
	private AcceptsOneWidget panel;

	@Inject
	public TweetDetailsActivity(CachingDispatcherAsync dispatcher,
	    EventBus eventBus,
	    TweetDetailsPlace place,
	    PlaceController placeController,
	    ApplicationContext ctx,
	    AsyncProvider<TweetDetailsView> viewProvider) {
		super(dispatcher, eventBus, placeController, ctx);
		this.place = place;
		this.viewProvider = viewProvider;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		checkAccountLogin();
	}

	private void fetchTweet(Long tweetId) {
		view.clearDisplay();
		GetCommunityWallDataAction action = new GetCommunityWallDataAction();
		action.setSearchType(SearchType.TWEET_BY_ID);
		action.setTweetId(tweetId.toString());
		dispatcher.execute(action, new DispatcherCallbackAsync<GetCommunityWallDataResult>() {

			@Override
			public void onSuccess(GetCommunityWallDataResult result) {
				if (result.getTweets().size() > 0) {
					view.display(result.getTweets().get(0));
				} else {
					view.displayMessage(StringConstants.FAILURE, AlertType.ERROR);
				}
			}

			@Override
			public void onFailure(Throwable th) {
				if (th instanceof NotFoundException) {
					view.displayMessage(StringConstants.INVALID_URL, AlertType.ERROR);
				} else {
					view.displayMessage(StringConstants.INTERNAL_ERROR, AlertType.ERROR);
				}
			}
		});
	}

	@Override
	protected void doStart() {
		HomePlace homePlace = new HomePlace();
		homePlace.setTweetId(place.getTweetId());
		placeController.goTo(homePlace);
	}

	@Override
	protected void doStartOnUserNotLoggedIn() {
		viewProvider.get(new AsyncCallback<TweetDetailsView>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(TweetDetailsView result) {
				TweetDetailsActivity.this.view = result;
				view.setPresenter(TweetDetailsActivity.this);
				if (place.getTweetId() != null) {
					fetchTweet(place.getTweetId());
				} else {
					placeController.goTo(new SignupPlace());
				}
				TweetDetailsActivity.this.panel.setWidget(view);
			}
		});
	}

	@Override
	public void sendMessage(ConversationDTO conversation) {
		placeController.goTo(new LoginPlace());
	}

	@Override
	public void go(AcceptsOneWidget container) {
	}

	@Override
	public void postComment(CommentDTO comment) {
		placeController.goTo(new LoginPlace());
	}

	@Override
	public void likeTweet(Long tweetId) {
		placeController.goTo(new LoginPlace());
	}

	@Override
	public void updateTweet(TweetDTO tweet) {
		placeController.goTo(new LoginPlace());
	}

	@Override
	public void deleteTweet(TweetDTO tweet) {
		placeController.goTo(new LoginPlace());
	}

	@Override
	public void displayPublicProfile(Long accountId) {
		placeController.goTo(new LoginPlace());
	}

	@Override
	public void sendTweet(TweetDTO tweet) {
		placeController.goTo(new LoginPlace());
	}

	@Override
	public void displayMessage(String msg, AlertType error) {
		view.displayMessage(msg, error);
	}

	@Override
	public void reportTweetAsSpam(TweetDTO tweet) {
		placeController.goTo(new LoginPlace());
	}

	@Override
	public void deleteImage(String url) {
		placeController.goTo(new LoginPlace());
	}

	@Override
	public void updateComment(CommentDTO comment) {
		placeController.goTo(new LoginPlace());
	}

	@Override
	public void bind() {
	}

	@Override
  public void checkCouponPurchaseEligibility(CouponDTO coupon, TweetWidget tweetWidget) {
		placeController.goTo(new LoginPlace());
  }

	@Override
  public void getCouponFormActionUrl(CouponFormWidget couponFormWidget) {
		placeController.goTo(new LoginPlace());
  }

	@Override
  public void initializeUploadForm(FormUploadWidget formUploadWidget) {
		placeController.goTo(new LoginPlace());
  }

	@Override
  public void purchaseCoupon(GoogleWalletSuccessResult result, CouponDTO coupon) {
		placeController.goTo(new LoginPlace());
  }
}
