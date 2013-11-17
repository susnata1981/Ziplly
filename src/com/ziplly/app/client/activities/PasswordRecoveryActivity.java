package com.ziplly.app.client.activities;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.places.PasswordRecoveryPlace;
import com.ziplly.app.client.view.PasswordRecoveryView;
import com.ziplly.app.client.view.PasswordRecoveryView.PasswordRecoveryPresenter;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.shared.ResetPasswordAction;
import com.ziplly.app.shared.ResetPasswordResult;
import com.ziplly.app.shared.SendPasswordRecoveryEmailAction;
import com.ziplly.app.shared.SendPasswordRecoveryEmailResult;
import com.ziplly.app.shared.VerifyPasswordRecoveryHashAction;
import com.ziplly.app.shared.VerifyPasswordRecoveryHashResult;

public class PasswordRecoveryActivity extends AbstractActivity implements
		PasswordRecoveryPresenter {
	private PasswordRecoveryView view;
	private PasswordRecoveryPlace place;
	private AccountDTO account;
	private AcceptsOneWidget panel;

	@Inject
	public PasswordRecoveryActivity(CachingDispatcherAsync dispatcher,
			EventBus eventBus, PlaceController placeController,
			ApplicationContext ctx, PasswordRecoveryPlace place,
			PasswordRecoveryView view) {
		super(dispatcher, eventBus, placeController, ctx);
		this.place = place;
		this.view = view;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		bind();
		this.panel = panel;
		if (place.getHash() != null) {
			verifyPasswordRecoveryHash();
		} else {
			view.displayPasswordRecoveryForm();
			panel.setWidget(view);
		}
	}

	private void verifyPasswordRecoveryHash() {
		dispatcher
				.execute(
						new VerifyPasswordRecoveryHashAction(place.getHash()),
						new DispatcherCallbackAsync<VerifyPasswordRecoveryHashResult>() {

							@Override
							public void onSuccess(
									VerifyPasswordRecoveryHashResult result) {
								account = result.getAccount();
								view.showMessage(StringConstants.PASSWORD_RESET_SUCCESFULLY, AlertType.SUCCESS);
								view.displayPasswordResetForm();
								panel.setWidget(view);
							}

							@Override
							public void onFailure(Throwable th) {
								view.showMessage(
										StringConstants.INVALID_ACCESS,
										AlertType.ERROR);
								view.displayPasswordRecoveryForm();
								panel.setWidget(view);
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
		dispatcher.execute(new SendPasswordRecoveryEmailAction(email),
				new DispatcherCallbackAsync<SendPasswordRecoveryEmailResult>() {
					@Override
					public void onSuccess(SendPasswordRecoveryEmailResult result) {
						view.showMessage(
								StringConstants.PASSWORD_RESET_LINK_SENT,
								AlertType.SUCCESS);
					}

					@Override
					public void onFailure(Throwable th) {
						if (th instanceof NotFoundException) {
							view.showMessage(StringConstants.INVALID_ACCESS,
									AlertType.ERROR);
							return;
						}
						view.showMessage(StringConstants.INTERNAL_ERROR,
								AlertType.ERROR);
					}
				});
	}

	@Override
	public void resetPassword(ResetPasswordAction action) {
		if (account != null) {
			action.setAccountId(account.getAccountId());
			dispatcher.execute(action,
					new DispatcherCallbackAsync<ResetPasswordResult>() {
						@Override
						public void onSuccess(ResetPasswordResult result) {
							view.showMessage(
								StringConstants.PASSWORD_RESET_SUCCESFULLY,
								AlertType.SUCCESS);
						}

						@Override
						public void onFailure(Throwable th) {
							if (th instanceof NotFoundException) {
								view.showMessage(
										StringConstants.INVALID_ACCESS,
										AlertType.ERROR);
								return;
							}
							view.showMessage(StringConstants.INTERNAL_ERROR,
									AlertType.ERROR);
						}
					});
		}
	}

	@Override
	public void fetchData() {
	}

	@Override
	public void go(AcceptsOneWidget container) {
	}
}
