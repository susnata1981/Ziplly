package com.ziplly.app.client.activities;

import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.AccountPlace;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.view.HomeView;
import com.ziplly.app.client.view.MainView;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.shared.GetCommunityWallDataAction;
import com.ziplly.app.shared.GetCommunityWallDataResult;
import com.ziplly.app.shared.GetLoggedInUserAction;
import com.ziplly.app.shared.GetLoggedInUserResult;

public class HomeActivity extends AbstractActivity implements HomePresenter {
	@Inject
	MainView mainView;
	@Inject
	HomeView homeView;
	
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
							getCommunitWallData();
						} else {
							updateUi();
						}
					}
				});
	}

	void getCommunitWallData() {
		dispatcher.execute(new GetCommunityWallDataAction(), new DispatcherCallbackAsync<GetCommunityWallDataResult>() {
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
	public void displayProfile(Long accountId) {
		placeController.goTo(new AccountPlace(accountId));
	}
}
