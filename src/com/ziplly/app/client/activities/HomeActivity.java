package com.ziplly.app.client.activities;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.constants.BackdropType;
import com.github.gwtbootstrap.client.ui.constants.IconSize;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.exceptions.AccessError;
import com.ziplly.app.client.exceptions.DuplicateException;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.view.HomeView;
import com.ziplly.app.client.view.IHomeView;
import com.ziplly.app.client.view.MainView;
import com.ziplly.app.client.view.event.LoginEvent;
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
import com.ziplly.app.shared.TweetAction;
import com.ziplly.app.shared.TweetResult;
import com.ziplly.app.shared.UpdateTweetAction;
import com.ziplly.app.shared.UpdateTweetResult;

public class HomeActivity extends AbstractActivity implements HomePresenter {
	@Inject
	MainView mainView;
	@Inject
	IHomeView homeView;
	
	HomePlace place;
	AccountDTO account;
	Modal modal = new Modal();
	HTMLPanel loadingPanel = new HTMLPanel("<span>Loading</span>");
	private AcceptsOneWidget panel;
	
	@Inject
	public HomeActivity(CachingDispatcherAsync dispatcher, 
			EventBus eventBus, 
			HomePlace place,
			PlaceController placeController,
			MainView mainView,
			ApplicationContext ctx,
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
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		setBackgroundImage();
		bind();
		showLodingIcon();
		if (ctx.getAccount() != null) {
			getCommunityWallData(TweetType.ALL);
		} else {
			fetchData();
			panel.setWidget(mainView);
		}
	}

	@Override
	public void onStop() {
		clearBackgroundImage();
		modal.hide();
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
		IHomeView view = ctx.getHomeView();
		if (view != null) {
			displayHomeView();
			return;
		}
		
		dispatcher.execute(new GetCommunityWallDataAction(type), new DispatcherCallbackAsync<GetCommunityWallDataResult>() {
			@Override
			public void onSuccess(GetCommunityWallDataResult result) {
				if (result != null) {
					hideLoadingIcon();
					ctx.setHomeView(homeView);
					HomeActivity.this.displayTweets(result.getTweets());
					clearBackgroundImage();
				}
			}
		});
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
	public void displayProfile(Long accountId) {
		placeController.goTo(new LoginPlace(accountId));
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
	}

	@Override
	public void tweet(TweetDTO tweet) {
		AccountDTO account = ctx.getAccount();
		if (account == null) {
			placeController.goTo(new LoginPlace());
		}
		
		dispatcher.execute(new TweetAction(tweet),
			new DispatcherCallbackAsync<TweetResult>() {
				@Override
				public void onSuccess(TweetResult result) {
					placeController.goTo(new HomePlace());
				}
			});
	}
}
