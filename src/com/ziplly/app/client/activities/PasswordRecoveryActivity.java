package com.ziplly.app.client.activities;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.exceptions.AccountExistsException;
import com.ziplly.app.client.exceptions.InvalidCredentialsException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.places.PasswordRecoveryPlace;
import com.ziplly.app.client.view.PasswordRecoveryView;
import com.ziplly.app.client.view.PasswordRecoveryView.PasswordRecoveryPresenter;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.shared.ResendEmailVerificationAction;
import com.ziplly.app.shared.ResendEmailVerificationResult;
import com.ziplly.app.shared.ResetPasswordAction;
import com.ziplly.app.shared.ResetPasswordResult;
import com.ziplly.app.shared.SendPasswordRecoveryEmailAction;
import com.ziplly.app.shared.SendPasswordRecoveryEmailResult;
import com.ziplly.app.shared.VerifyPasswordRecoveryHashAction;
import com.ziplly.app.shared.VerifyPasswordRecoveryHashResult;

public class PasswordRecoveryActivity extends AbstractActivity implements PasswordRecoveryPresenter {
	private PasswordRecoveryView view;
	private PasswordRecoveryPlace place;
	private AccountDTO account;
	private AcceptsOneWidget panel;
	private AsyncProvider<PasswordRecoveryView> viewProvider;

	@Inject
	public PasswordRecoveryActivity(CachingDispatcherAsync dispatcher,
	    EventBus eventBus,
	    PlaceController placeController,
	    ApplicationContext ctx,
	    PasswordRecoveryPlace place,
	    AsyncProvider<PasswordRecoveryView> viewProvider) {
		super(dispatcher, eventBus, placeController, ctx);
		this.place = place;
		this.viewProvider = viewProvider;
	}

	@Override
	public void start(final AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		viewProvider.get(new DefaultViewLoaderAsyncCallback<PasswordRecoveryView>() {

			@Override
			public void onSuccess(PasswordRecoveryView result) {
				PasswordRecoveryActivity.this.view = result;
				PasswordRecoveryActivity.this.panel.setWidget(view);
				bind();
				if (place.getHash() != null) {
					verifyPasswordRecoveryHash();
				} else {
					view.displayPasswordRecoveryForm();
				}
		    panel.setWidget(view);
			}
		});
	}

	@Override
	public void doStart() {
		// shoudn't get called.
	}

	private void verifyPasswordRecoveryHash() {
		dispatcher.execute(
		    new VerifyPasswordRecoveryHashAction(place.getHash()),
		    new DispatcherCallbackAsync<VerifyPasswordRecoveryHashResult>() {

			    @Override
			    public void onSuccess(VerifyPasswordRecoveryHashResult result) {
				    account = result.getAccount();
				    view.showMessage(StringConstants.PASSWORD_RESET_SUCCESFULLY, AlertType.SUCCESS);
				    view.displayPasswordResetForm();
			    }

			    @Override
			    public void onFailure(Throwable th) {
				    view.showMessage(StringConstants.INVALID_ACCESS, AlertType.ERROR);
				    view.displayPasswordRecoveryForm();
			    }
		    });
	}

	@Override
	public void bind() {
		view.setPresenter(this);
	}

	@Override
	public void emailPasswordResetLink(String email) {
		// do something
		dispatcher.execute(
		    new SendPasswordRecoveryEmailAction(email),
		    new DispatcherCallbackAsync<SendPasswordRecoveryEmailResult>() {
			    @Override
			    public void onSuccess(SendPasswordRecoveryEmailResult result) {
				    view.showMessage(StringConstants.PASSWORD_RESET_LINK_SENT, AlertType.SUCCESS);
			    }

			    @Override
			    public void onFailure(Throwable th) {
				    if (th instanceof NotFoundException) {
					    view.showMessage(StringConstants.INVALID_ACCESS, AlertType.ERROR);
					    return;
				    }
				    view.showMessage(StringConstants.INTERNAL_ERROR, AlertType.ERROR);
			    }
		    });
	}

	@Override
	public void resetPassword(ResetPasswordAction action) {
		if (account != null) {
			action.setAccountId(account.getAccountId());
			dispatcher.execute(action, new DispatcherCallbackAsync<ResetPasswordResult>() {
				@Override
				public void onSuccess(ResetPasswordResult result) {
					view.showMessage(StringConstants.PASSWORD_RESET_SUCCESFULLY, AlertType.SUCCESS);
				}

				@Override
				public void onFailure(Throwable th) {
					if (th instanceof NotFoundException) {
						view.showMessage(StringConstants.INVALID_ACCESS, AlertType.ERROR);
					} else if (th instanceof InvalidCredentialsException) {
						view.showMessage(StringConstants.ACCOUNT_DOESNT_EXISTS, AlertType.ERROR);
					} else {
						view.showMessage(StringConstants.INTERNAL_ERROR, AlertType.ERROR);
					}
				}
			});
		}
	}

	@Override
	public void resendVerficationEmail(String email) {
		dispatcher.execute(
		    new ResendEmailVerificationAction(email),
		    new DispatcherCallbackAsync<ResendEmailVerificationResult>() {

			    @Override
			    public void onSuccess(ResendEmailVerificationResult result) {
				    view.showMessage(StringConstants.VERIFICATION_EMAIL_SENT, AlertType.SUCCESS);
			    }

			    @Override
			    public void onFailure(Throwable th) {
				    if (th instanceof NotFoundException) {
					    view.showMessage(StringConstants.ACCOUNT_DOESNT_EXISTS, AlertType.ERROR);
				    } else if (th instanceof AccountExistsException) {
					    view.showMessage(StringConstants.ACCOUNT_ALEADY_VERIFIED, AlertType.WARNING);
				    } else {
					    // InternalError, Exception
					    view.showMessage(StringConstants.FAILURE_SENDING_VERIFICATION_EMAIL, AlertType.ERROR);
				    }
			    }
		    });
	}

	@Override
	public void go(AcceptsOneWidget container) {
	}
}
