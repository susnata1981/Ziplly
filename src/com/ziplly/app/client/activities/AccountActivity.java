package com.ziplly.app.client.activities;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.AccountPlace;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.view.AccountView;
import com.ziplly.app.client.view.event.AccountUpdateEvent;
import com.ziplly.app.client.view.event.LogoutEvent;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.shared.GetAccountByIdAction;
import com.ziplly.app.shared.GetAccountByIdResult;
import com.ziplly.app.shared.GetLoggedInUserAction;
import com.ziplly.app.shared.GetLoggedInUserResult;
import com.ziplly.app.shared.LogoutAction;
import com.ziplly.app.shared.LogoutResult;
import com.ziplly.app.shared.TweetAction;
import com.ziplly.app.shared.TweetResult;
import com.ziplly.app.shared.UpdateAccountAction;
import com.ziplly.app.shared.UpdateAccountResult;

public class AccountActivity extends AbstractLoginAwareActivity<AccountView>
		implements AccountActivityPresenter {

	AccountPlace place;
	protected AccountDTO account;
	private Logger logger = Logger.getLogger(AccountActivity.class.getName());
	private AcceptsOneWidget panel;

	@Inject
	public AccountActivity(CachingDispatcherAsync dispatcher,
			EventBus eventBus, AccountPlace place,
			PlaceController placeController, AccountView accountView) {
		super(dispatcher, eventBus, placeController, accountView);
		this.place = place;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		bind();
		fetchData();
	}

	@Override
	public void go(AcceptsOneWidget container) {
		container.setWidget(view);
	}

	@Override
	public void bind() {
		view.setPresenter(this);
	}

	@Override
	public void updateUi() {
		go(panel);
	}

	@Override
	public void onStop() {
		view.clear();
	}

	@Override
	public void fetchData() {
		final Long accountId = place.getAccountId();
		if (accountId != null) {
			dispatcher.execute(new GetAccountByIdAction(accountId),
					new DispatcherCallbackAsync<GetAccountByIdResult>() {
						@Override
						public void onSuccess(GetAccountByIdResult result) {
							if (result == null || result.getAccount() == null) {
								logger.log(Level.WARNING,
										"Accessing invalid account id "
												+ accountId);
								// TODO display an error page;
								Window.alert("Invalid account");
								return;
							}
							AccountDTO acct = result.getAccount();
							if (acct instanceof PersonalAccountDTO) {
								AccountActivity.this
										.displayPublicProfile((PersonalAccountDTO) acct);

							} else {
								// business
							}
							updateUi();
						}
					});
		} else {
			dispatcher.execute(new GetLoggedInUserAction(),
					new DispatcherCallbackAsync<GetLoggedInUserResult>() {
						@Override
						public void onSuccess(GetLoggedInUserResult result) {
							if (result != null && result.getAccount() != null) {
								AccountDTO acct = result.getAccount();
								if (acct instanceof PersonalAccountDTO) {
									displayProfile((PersonalAccountDTO) acct);
								} else if (acct instanceof BusinessAccountDTO) {
									displayProfile((BusinessAccountDTO) acct);
								} 
								 else {
									view.displayLoginWidget();
								}
							} else {
								view.displayLoginWidget();
							}
							
							updateUi();
						}
					});
		}
	}

	public void displayProfile(PersonalAccountDTO account) {
		this.account = account;
		view.displayProfile(account);
		view.displayLogoutWidget();
	}

	public void displayProfile(BusinessAccountDTO account) {
		this.account = account;
		// TODO
	}
	
	@Override
	public void displayPublicProfile(PersonalAccountDTO account) {
		view.displayPublicProfile((PersonalAccountDTO) account);
	}

	public void displayPublicProfile(Long accountId) {
		placeController.goTo(new AccountPlace(accountId));
	}

	public void logout() {
		dispatcher.execute(new LogoutAction(account.getUid()),
				new DispatcherCallbackAsync<LogoutResult>() {
					@Override
					public void onSuccess(LogoutResult result) {
						eventBus.fireEvent(new LogoutEvent());
						view.clear();
						goTo(new HomePlace());
					}
				});
	}

	public void onLogin(String email, String password) {
		validateLogin(email, password);
	}

	public void save(PersonalAccountDTO account) {
		dispatcher.execute(new UpdateAccountAction(account),
				new DispatcherCallbackAsync<UpdateAccountResult>() {
					@Override
					public void onSuccess(UpdateAccountResult result) {
						view.displayAccountUpdateSuccessfullMessage();
						eventBus.fireEvent(new AccountUpdateEvent(result
								.getAccount()));
					}

					public void onFailure(Throwable error) {
						System.out.println(error.getMessage());
						view.displayAccountUpdateFailedMessage();
					}
				});
	}

	public void tweet(final TweetDTO tweet) {
		if (account == null) {
			placeController.goTo(new AccountPlace());
		}
		dispatcher.execute(new TweetAction(tweet),
				new DispatcherCallbackAsync<TweetResult>() {
					@Override
					public void onSuccess(TweetResult result) {
						placeController.goTo(new HomePlace());
						AccountActivity.this.view.clearTweet();
					}
				});
	}
}
