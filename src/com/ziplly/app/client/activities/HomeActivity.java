package com.ziplly.app.client.activities;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.BackdropType;
import com.github.gwtbootstrap.client.ui.constants.IconSize;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HTMLPanel;
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
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.shared.CommentAction;
import com.ziplly.app.shared.CommentResult;
import com.ziplly.app.shared.DeleteTweetAction;
import com.ziplly.app.shared.DeleteTweetResult;
import com.ziplly.app.shared.GetCommunityWallDataAction;
import com.ziplly.app.shared.GetCommunityWallDataResult;
import com.ziplly.app.shared.GetLoggedInUserAction;
import com.ziplly.app.shared.GetLoggedInUserResult;
import com.ziplly.app.shared.LikeResult;
import com.ziplly.app.shared.LikeTweetAction;
import com.ziplly.app.shared.TweetAction;
import com.ziplly.app.shared.TweetResult;
import com.ziplly.app.shared.UpdateTweetAction;
import com.ziplly.app.shared.UpdateTweetResult;
import com.ziplly.app.shared.ValidateLoginAction;
import com.ziplly.app.shared.ValidateLoginResult;

public class HomeActivity extends AbstractActivity implements HomePresenter, InfiniteScrollHandler {
	MainView mainView;
	IHomeView homeView;
	private List<TweetDTO> lastTweetList;
	private int tweetPageIndex = 0;
	private int pageSize = 3;

	public static interface IHomeView extends View<HomePresenter> {
		void display(List<TweetDTO> tweets);

		void updateTweet(TweetDTO tweet);

		void removeTweet(TweetDTO tweet);

		void updateComment(CommentDTO comment);

		void updateTweetLike(LoveDTO like);

		void updateTweets(List<TweetDTO> tweets);

		Element getTweetSectionElement();

		void displayMessage(String message, AlertType error);
	}

	HomePlace place;
	AccountDTO account;
	Modal modal = new Modal();
	HTMLPanel loadingPanel = new HTMLPanel("<span>Loading</span>");
	private AcceptsOneWidget panel;
	private TweetType tweetType;

	@Inject
	public HomeActivity(CachingDispatcherAsync dispatcher, EventBus eventBus, HomePlace place,
			PlaceController placeController, MainView mainView, ApplicationContext ctx,
			HomeView homeView) {

		super(dispatcher, eventBus, placeController, ctx);
		this.place = place;
		this.homeView = homeView;
		this.mainView = mainView;
		Icon loadingIcon = new Icon();
		loadingIcon.setIcon(IconType.SPINNER);
		loadingIcon.setIconSize(IconSize.LARGE);
		loadingIcon.setVisible(true);
		modal.getElement().getStyle().setMarginLeft(0, Unit.PX);
		loadingPanel.add(loadingIcon);
		modal.add(loadingPanel);
		modal.setAnimation(true);
		modal.setWidth("100px");
		modal.setBackdrop(BackdropType.NONE);
	}

	void showLodingIcon() {
		loadingPanel.setVisible(true);
		modal.show();
	}

	void hideLoadingIcon() {
		loadingPanel.setVisible(false);
		modal.hide();
	}

	/*
	 * Hack to deal with absolute layout : TODO(shaan)
	 */
	public static native void hideFooter() /*-{
		$doc.getElementById("footer").style.display = 'none';
	}-*/;

	/*
	 * Hack to deal with absolute layout : TODO(shaan)
	 */
	public static native void showFooter() /*-{
		$doc.getElementById("footer").style.display = 'block';
	}-*/;

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		setBackgroundImage();
		bind();
		showLodingIcon();
		if (ctx.getAccount() != null) {
			getCommunityWallData(TweetType.ALL);
			hideFooter();
		} else {
			fetchData();
			panel.setWidget(mainView);
		}
	}

	@Override
	public void onStop() {
		clearBackgroundImage();
		modal.hide();
		mainView.clear();
		homeView.clear();
		showFooter();
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
		clearBackgroundImage();
		hideLoadingIcon();
		panel.setWidget(homeView);
	}

	@Override
	public void fetchData() {
		dispatcher.execute(new GetLoggedInUserAction(),
				new DispatcherCallbackAsync<GetLoggedInUserResult>() {
					@Override
					public void onSuccess(GetLoggedInUserResult result) {
						if (result != null && result.getAccount() != null) {
							eventBus.fireEvent(new LoginEvent(result.getAccount()));
							forward(result.getAccount());
						} else {
							displayMainView();
						}
						hideLoadingIcon();
					}
				});
	}

	void getCommunityWallData(TweetType type) {
		// load first batch of data
		this.tweetType = type;
		tweetPageIndex = 0;
		lastTweetList = null;
		// homeView.clear();
		showLodingIcon();
		dispatcher.execute(new GetCommunityWallDataAction(type, tweetPageIndex, pageSize),
				new CommunityDataHandler());
		TweetViewBinder binder = new TweetViewBinder(homeView.getTweetSectionElement(), this);
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
		dispatcher.execute(action, new DispatcherCallbackAsync<LikeResult>() {

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
		});
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
		if (lastTweetList == null) {
			return true;
		}
		return lastTweetList.size() == pageSize;
	}

	@Override
	public void onScrollBottomHit() {
		tweetPageIndex++;
		GetCommunityWallDataAction action = null;
		if (ctx.getAccount() != null) {
			action = new GetCommunityWallDataAction(tweetType, tweetPageIndex, pageSize);
		}
		dispatcher.execute(action, new DispatcherCallbackAsync<GetCommunityWallDataResult>() {

			@Override
			public void onSuccess(GetCommunityWallDataResult result) {
				lastTweetList = result.getTweets();
				homeView.updateTweets(result.getTweets());
			}
		});
	}

	// ----------------------------------	
	//	
	//	Action Handlers are defined here
	//	
	// ----------------------------------
	private class TweetHandler extends DispatcherCallbackAsync<TweetResult> {
		@Override
		public void onSuccess(TweetResult result) {
			placeController.goTo(new HomePlace());
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
			homeView.displayMessage(StringConstants.FAILED_TO_UPDATE_COMMENT,
					AlertType.SUCCESS);
		}
	};
}
