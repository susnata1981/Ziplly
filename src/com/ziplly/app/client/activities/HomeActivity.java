package com.ziplly.app.client.activities;

import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.exceptions.AccessError;
import com.ziplly.app.client.exceptions.DuplicateException;
import com.ziplly.app.client.places.AccountPlace;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.view.HomeView;
import com.ziplly.app.client.view.IHomeView;
import com.ziplly.app.client.view.MainView;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.shared.CommentAction;
import com.ziplly.app.shared.CommentResult;
import com.ziplly.app.shared.GetCommunityWallDataAction;
import com.ziplly.app.shared.GetCommunityWallDataResult;
import com.ziplly.app.shared.GetLoggedInUserAction;
import com.ziplly.app.shared.GetLoggedInUserResult;
import com.ziplly.app.shared.LikeResult;
import com.ziplly.app.shared.LikeTweetAction;
import com.ziplly.app.shared.UpdateTweetAction;
import com.ziplly.app.shared.UpdateTweetResult;

public class HomeActivity extends AbstractActivity implements HomePresenter {
	@Inject
	MainView mainView;
	@Inject
	IHomeView homeView;
	
	HomePlace place;
	AcceptsOneWidget container;
	AccountDTO account;

	@Inject
	public HomeActivity(CachingDispatcherAsync dispatcher, 
			EventBus eventBus, 
			HomePlace place,
			PlaceController placeController,
			MainView mainView,
			HomeView homeView) {
		super(dispatcher, eventBus, placeController);
		this.place = place;
		this.homeView = homeView;
		this.mainView = mainView;		
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		bind();
		go(panel);
	}

	@Override
	public void go(AcceptsOneWidget container) {
		this.container = container;
		bind();
		fetchData();
	}

	@Override
	public void bind() {
		mainView.setPresenter(this);
		homeView.setPresenter(this);
	}

	void displayMainView() {
		container.setWidget(mainView);
	}
	
	void displayHomeView() {
		container.setWidget(homeView);
	}
	
	@Override
	public void fetchData() {
		dispatcher.execute(new GetLoggedInUserAction(),
				new DispatcherCallbackAsync<GetLoggedInUserResult>() {

					@Override
					public void onSuccess(GetLoggedInUserResult result) {
						if (result != null && result.getAccount() != null) {
							HomeActivity.this.account = result.getAccount();
							getCommunitWallData(TweetType.ALL);
						} else {
							updateUi();
						}
					}
				});
	}

	void getCommunitWallData(TweetType type) {
		dispatcher.execute(new GetCommunityWallDataAction(type), new DispatcherCallbackAsync<GetCommunityWallDataResult>() {
			@Override
			public void onSuccess(GetCommunityWallDataResult result) {
				if (result != null) {
					HomeActivity.this.displayTweets(result.getTweets());
				}
				updateUi();
			}
		});
	}
	
	@Override
	public void updateUi() {
		if (this.account != null) {
			displayHomeView();
		} else {
			displayMainView();
		}
	}

	@Override
	public void displayTweets(List<TweetDTO> tweets) {
		homeView.display(tweets);
	}


	@Override
	public void displayTweetsForCategory(TweetType type) {
		getCommunitWallData(type);
	}
	
	@Override
	public void displayProfile(Long accountId) {
		placeController.goTo(new AccountPlace(accountId));
	}

	@Override
	public void postComment(final CommentDTO comment) {
		dispatcher.execute(new CommentAction(comment), new DispatcherCallbackAsync<CommentResult>() {
			@Override
			public void onSuccess(CommentResult result) {
				homeView.displayCommentSuccessfull();
				homeView.updateComment(comment);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				homeView.displayCommentFailure();
			}
		});
	}
	
	@Override
	public void likeTweet(Long tweetId) {
		LikeTweetAction action = new LikeTweetAction();
		action.setTweetId(tweetId);
		dispatcher.execute(action, new DispatcherCallbackAsync<LikeResult>() {

			@Override
			public void onSuccess(LikeResult result) {
				homeView.displayLikeSuccessful();
				homeView.updateTweetLike(result.getLike());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof DuplicateException) {
					homeView.displayLikeUnsuccessful();
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
		
		dispatcher.execute(new UpdateTweetAction(tweet), new DispatcherCallbackAsync<UpdateTweetResult>() {
			@Override
			public void onSuccess(UpdateTweetResult result) {
				homeView.updateTweet(result.getTweet());
				homeView.displayTweetUpdated();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof AccessError) {
					homeView.displayInvalidAccessMessage();
					return;
				} 
				homeView.displayInternalError();
			}
		});
	}

	@Override
	public void deleteTweet(TweetDTO tweet) {
		// TODO Auto-generated method stub
		
	}
}
