package com.ziplly.app.client.activities;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.view.AdminView;
import com.ziplly.app.client.view.AdminView.AdminPresenter;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountSearchCriteria;
import com.ziplly.app.model.AccountType;
import com.ziplly.app.model.BusinessType;
import com.ziplly.app.model.Role;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetSearchCriteria;
import com.ziplly.app.shared.CreateRegistrationAction;
import com.ziplly.app.shared.CreateRegistrationResult;
import com.ziplly.app.shared.GetTweetsAction;
import com.ziplly.app.shared.GetTweetsResult;
import com.ziplly.app.shared.SearchAccountAction;
import com.ziplly.app.shared.SearchAccountResult;
import com.ziplly.app.shared.UpdateAccountAction;
import com.ziplly.app.shared.UpdateAccountResult;
import com.ziplly.app.shared.UpdateTweetAction;
import com.ziplly.app.shared.UpdateTweetResult;

public class AdminActivity extends AbstractActivity implements AdminPresenter {

	private AdminView view;
	private AsyncProvider<AdminView> viewProvider;
	private AcceptsOneWidget panel;

	public AdminActivity(
			CachingDispatcherAsync dispatcher, 
			EventBus eventBus,
			PlaceController placeController, 
			ApplicationContext ctx, 
			AsyncProvider<AdminView> viewProvider) {
		
		super(dispatcher, eventBus, placeController, ctx);
		this.viewProvider = viewProvider;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		AccountDTO account = ctx.getAccount();
		if (account == null) {
			placeController.goTo(new LoginPlace());
			return;
		}
		if (account.getRole() != Role.ADMINISTRATOR) {
			// do something;
			// messaging
			view.displayMessage(StringConstants.INVALID_ACCESS, AlertType.ERROR);
			return;
		} else {
			viewProvider.get(new AsyncCallback<AdminView>() {

				@Override
				public void onFailure(Throwable caught) {
				}

				@Override
				public void onSuccess(AdminView result) {
					AdminActivity.this.view = result;
					bind();
				}
			});
		}
	}
	
	@Override
	public void onStop() {
		view.clear();
	}

	
	@Override
	public void bind() {
		view.setPresenter(this);
		panel.setWidget(view);
	}

	@Override
	public void searchTweets(final int start, int end, TweetSearchCriteria tsc) {
		GetTweetsAction action = new GetTweetsAction();
		action.setCriteria(tsc);
		action.setStart(start);
		action.setEnd(end);
		dispatcher.execute(action , new DispatcherCallbackAsync<GetTweetsResult>() {
			@Override
			public void onSuccess(GetTweetsResult result) {
				if (result != null) {
					view.setTweetData(start, result.getTweets());
					view.setTweetRowCount(result.getTotalTweetCount().intValue());
				}
			}
		});
	}

	@Override
	public void update(TweetDTO tweet) {
		dispatcher.execute(new UpdateTweetAction(tweet), new DispatcherCallbackAsync<UpdateTweetResult>() {
			@Override
			public void onSuccess(UpdateTweetResult result) {
				view.refresh();
			}
		});
	}

	@Override
	public void update(AccountDTO account) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void searchAccounts(final int start, int end, AccountSearchCriteria asc) {
		SearchAccountAction action = new SearchAccountAction(asc);
		action.setStart(start);
		action.setEnd(end);
		action.setCriteria(asc);
		dispatcher.execute(action, new DispatcherCallbackAsync<SearchAccountResult>() {
			@Override
			public void onSuccess(SearchAccountResult result) {
				if (result != null) {
					view.setAccountData(start, result.getAccounts());
					view.setAccountRowCount(result.getTotalAccounts());
				}
			}
		});
	}

	
	@Override
	public void fetchData() {
	}

	@Override
	public void go(AcceptsOneWidget container) {
	}

	@Override
	public void inviteForRegistration(String email, AccountType type, BusinessType btype) {
		if (email != null) {
			dispatcher.execute(new CreateRegistrationAction(email, type, btype), new DispatcherCallbackAsync<CreateRegistrationResult>() {
				@Override
				public void onSuccess(CreateRegistrationResult result) {
					view.displayMessage(StringConstants.MESSAGE_SENT, AlertType.SUCCESS);
				}
				public void onFailure(Throwable th) {
					view.displayMessage(StringConstants.INTERNAL_ERROR, AlertType.ERROR);
				}
			});
		}
	}

	@Override
	public void updateAccount(AccountDTO account) {
		if (account != null) {
			dispatcher.execute(new UpdateAccountAction(account), new DispatcherCallbackAsync<UpdateAccountResult>() {
				@Override
				public void onSuccess(UpdateAccountResult result) {
					view.displayMessage(StringConstants.ACCOUNT_SAVE_SUCCESSFUL, AlertType.SUCCESS);
				}
			});
		}
	}
}
