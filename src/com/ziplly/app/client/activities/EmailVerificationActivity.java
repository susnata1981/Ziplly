package com.ziplly.app.client.activities;

import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.exceptions.AccessError;
import com.ziplly.app.client.exceptions.AccountExistsException;
import com.ziplly.app.client.exceptions.DuplicateException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.places.EmailVerificationPlace;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.view.EmailVerificationView;
import com.ziplly.app.client.view.EmailVerificationView.EmailVerificationPresenter;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.shared.ResendEmailVerificationAction;
import com.ziplly.app.shared.ResendEmailVerificationResult;
import com.ziplly.app.shared.VerifyEmailAction;
import com.ziplly.app.shared.VerifyEmailResult;

public class EmailVerificationActivity extends AbstractActivity implements EmailVerificationPresenter {
	private EmailVerificationView view;
	private AsyncProvider<EmailVerificationView> viewProvider;
	private EmailVerificationPlace place;
	private AcceptsOneWidget panel;

	@Inject
	public EmailVerificationActivity(CachingDispatcherAsync dispatcher, EventBus eventBus,
			PlaceController placeController, ApplicationContext ctx, EmailVerificationPlace place,
			AsyncProvider<EmailVerificationView> viewProvider) {
		super(dispatcher, eventBus, placeController, ctx);
		this.viewProvider = viewProvider;
		this.place = place;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		viewProvider.get(new DefaultViewLoaderAsyncCallback<EmailVerificationView>() {

			@Override
			public void onSuccess(EmailVerificationView result) {
				try {
					EmailVerificationActivity.this.view = result;
					EmailVerificationActivity.this.panel.setWidget(view);
					bind();
					if (place.getCode() != null && place.getId() != null) {
						VerifyEmailAction action = new VerifyEmailAction(Long.parseLong(place.getId()),
								place.getCode());
						dispatcher.execute(action, new EmailVerificationHandler());
					} else {
						view.displayMessage(StringConstants.INVALID_URL, AlertType.ERROR);
					}
				} catch (RuntimeException ex) {
					view.displayMessage(StringConstants.INVALID_URL, AlertType.ERROR);
				}
			}
		});
	}

	@Override
	protected void doStart() {
		goTo(new HomePlace());
	}

	private class EmailVerificationHandler extends DispatcherCallbackAsync<VerifyEmailResult> {

		@Override
		public void onSuccess(VerifyEmailResult result) {
			view.displayMessage(StringConstants.EMAIL_VERIFICATION_SUCCESSFUL, AlertType.SUCCESS);
			view.displaySuccessPanel();
		}

		@Override
		public void onFailure(Throwable th) {
			if (th instanceof AccessError) {
				view.displayMessage(StringConstants.INVALID_ACCESS, AlertType.ERROR);
				view.displayFailurePanel();
			}
			else if (th instanceof DuplicateException) {
				view.displayMessage(StringConstants.ACCOUNT_ALEADY_VERIFIED, AlertType.WARNING);
				view.displaySuccessPanel();
			}
			else {
				view.displayMessage(StringConstants.FAILURE, AlertType.ERROR);
				view.displayFailurePanel();
			}
		}
	}

	@Override
	public void bind() {
		this.view.setPresenter(this);
	}
	
	@Override
	public void go(AcceptsOneWidget container) {
	}

	@Override
	public void resendEmailVerification(String email) {
		dispatcher.execute(new ResendEmailVerificationAction(email), new DispatcherCallbackAsync<ResendEmailVerificationResult>() {

			@Override
			public void onSuccess(ResendEmailVerificationResult result) {
				view.displayMessage(StringConstants.EMAIL_VERIFICATION_RESEND_SUCCESSFUL, AlertType.SUCCESS);
			}
			
			@Override
			public void onFailure(Throwable th) {
				if (th instanceof AccountExistsException) {
					view.displayMessage(StringConstants.ACCOUNT_ALEADY_VERIFIED, AlertType.WARNING);
				}
				if (th instanceof NotFoundException) {
					view.displayMessage(StringConstants.ACCOUNT_DOESNT_EXISTS, AlertType.ERROR);
				} 
				else {
					view.displayMessage(StringConstants.INTERNAL_ERROR, AlertType.ERROR);
				}
			}
		});
	}
}
