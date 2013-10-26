package com.ziplly.app.client.activities;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.exceptions.InvalidCredentialsException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.view.event.AccountUpdateEvent;
import com.ziplly.app.client.view.event.LogoutEvent;
import com.ziplly.app.client.widget.LoginWidget;
import com.ziplly.app.model.AccountDTO;
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
import com.ziplly.app.shared.ValidateLoginAction;
import com.ziplly.app.shared.ValidateLoginResult;

public class AccountActivity2 extends AbstractActivity implements AccountActivityPresenter {
	LoginPlace place;
	protected AccountDTO account;
	private Logger logger = Logger.getLogger(AccountActivity2.class.getName());
	private AcceptsOneWidget panel;
//	private IAccountView<? extends AccountDTO> view;
	private IAccountViewRenderer<? extends AccountDTO> renderer;
	
	@Inject
	public AccountActivity2(CachingDispatcherAsync dispatcher,
			EventBus eventBus, LoginPlace place,
			PlaceController placeController,
			ApplicationContext ctx) {
		super(dispatcher, eventBus, placeController, ctx);
		this.place = place;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		fetchData();
	}

	@Override
	public void go(AcceptsOneWidget container) {
		container.setWidget(renderer.getWidget());
	}

	@Override
	public void bind() {
		renderer.setPresenter(this);
	}

	@Override
	public void updateUi() {
		go(panel);
	}

	@Override
	public void onStop() {
		renderer.clear();
	}

	@Override
	public void fetchData() {
		final Long accountId = place.getAccountId();
		if (accountId != null) {
			displayAccountForId(accountId);
		} else {
			checkIfUserLoggedIn();
		}
	}

	private void checkIfUserLoggedIn() {
		dispatcher.execute(new GetLoggedInUserAction(),
				new DispatcherCallbackAsync<GetLoggedInUserResult>() {
					@Override
					public void onSuccess(GetLoggedInUserResult result) {
						bindAndSetView(result.getAccount());
						if (result != null && result.getAccount() != null) {
							displayProfile(result.getAccount());
						} else {
							renderer.displayLoginWidget();
						}
						
						updateUi();
					}
				});
	}

	private void displayAccountForId(final Long accountId) {
		dispatcher.execute(new GetAccountByIdAction(accountId),
				new DispatcherCallbackAsync<GetAccountByIdResult>() {
					@Override
					public void onSuccess(GetAccountByIdResult result) {
						bindAndSetView(result.getAccount());
						if (result == null || result.getAccount() == null) {
							logger.log(Level.WARNING,
									"Accessing invalid account id "+ accountId);
							// TODO display an error page;
							Window.alert("Invalid account");
							return;
						}
						displayPublicProfile(result.getAccount());
						updateUi();
					}
				});
	}

	void bindAndSetView(AccountDTO account) {
		renderer = AccountViewRendererFactory.getAccountViewRenderer(account,AccountActivity2.this);
		bind();
	}
	
	@Override
	public void displayProfile(AccountDTO account) {
		this.account = account;
		AccountViewRendererFactory.getAccountViewRenderer(account,this).displayProfile(account);
	}

	@Override
	public void displayPublicProfile(AccountDTO account) {
		AccountViewRendererFactory.getAccountViewRenderer(account,this).displayPublicProfile(account);
	}

	@Override
	public void displayPublicProfile(Long accountId) {
		placeController.goTo(new LoginPlace(accountId));
	}

	@Override
	public void logout() {
		dispatcher.execute(new LogoutAction(account.getUid()),
				new DispatcherCallbackAsync<LogoutResult>() {
					@Override
					public void onSuccess(LogoutResult result) {
						eventBus.fireEvent(new LogoutEvent());
						renderer.clear();
						goTo(new LoginPlace());
					}
				});
	}

	@Override
	public void onLogin(String email, String password) {
		validateLogin(email, password);
	}

	@Override
	public void save(PersonalAccountDTO account) {
		dispatcher.execute(new UpdateAccountAction(account),
				new DispatcherCallbackAsync<UpdateAccountResult>() {
					@Override
					public void onSuccess(UpdateAccountResult result) {
						renderer.displayAccountUpdateSuccessfullMessage();
						eventBus.fireEvent(new AccountUpdateEvent(result
								.getAccount()));
					}

					public void onFailure(Throwable error) {
//						System.out.println(error.getMessage());
						renderer.displayAccountUpdateFailedMessage();
					}
				});
	}

	@Override
	public void tweet(final TweetDTO tweet) {
		if (account == null) {
			placeController.goTo(new LoginPlace());
		}
		dispatcher.execute(new TweetAction(tweet),
				new DispatcherCallbackAsync<TweetResult>() {
					@Override
					public void onSuccess(TweetResult result) {
						placeController.goTo(new HomePlace());
//						AccountActivity.this.view.clearTweet();
					}
				});
	}
	
	public void validateLogin(String email, String password) {
		dispatcher.execute(new ValidateLoginAction(email, password),
		new DispatcherCallbackAsync<ValidateLoginResult>() {
			@Override
			public void onSuccess(ValidateLoginResult result) {
				if (result != null) {
					goTo(new LoginPlace());
//					bindAndSetView(result.getAccount());
//					displayProfile(result.getAccount());
				} else {
					renderer.displayLoginErrorMessage(LoginWidget.INVALID_ACCOUNT_CREDENTIALS, AlertType.ERROR);
				}
				renderer.resetLoginForm();
			}

			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof NotFoundException) {
					renderer.displayLoginErrorMessage(LoginWidget.ACCOUNT_DOES_NOT_EXIST, AlertType.ERROR);
				} else if (caught instanceof InvalidCredentialsException) {
					renderer.displayLoginErrorMessage(LoginWidget.INVALID_ACCOUNT_CREDENTIALS, AlertType.ERROR);
				}
				renderer.resetLoginForm();
			}
		});
	}

	@Override
	public void goTo(Place place) {
		// TODO Auto-generated method stub
		
	}
}
