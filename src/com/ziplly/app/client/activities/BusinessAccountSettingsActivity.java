package com.ziplly.app.client.activities;

import java.util.List;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.exceptions.DuplicateException;
import com.ziplly.app.client.exceptions.InvalidCredentialsException;
import com.ziplly.app.client.places.BusinessAccountPlace;
import com.ziplly.app.client.places.ConversationPlace;
import com.ziplly.app.client.places.PersonalAccountSettingsPlace;
import com.ziplly.app.client.view.BusinessAccountSettingsView.BusinessAccountSettingsPresenter;
import com.ziplly.app.client.view.ISettingsView;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.view.handler.LoginEventHandler;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.SubscriptionPlanDTO;
import com.ziplly.app.model.TransactionDTO;
import com.ziplly.app.model.TransactionStatus;
import com.ziplly.app.shared.GetAllSubscriptionPlanAction;
import com.ziplly.app.shared.GetAllSubscriptionPlanResult;
import com.ziplly.app.shared.GetJwtTokenAction;
import com.ziplly.app.shared.GetJwtTokenResult;
import com.ziplly.app.shared.PayAction;
import com.ziplly.app.shared.PayResult;
import com.ziplly.app.shared.UpdatePasswordAction;
import com.ziplly.app.shared.UpdatePasswordResult;

public class BusinessAccountSettingsActivity
		extends
		AbstractAccountSettingsActivity<BusinessAccountDTO, BusinessAccountSettingsActivity.IBusinessAccountSettingView>
		implements BusinessAccountSettingsPresenter {

	public static interface IBusinessAccountSettingView extends
			ISettingsView<BusinessAccountDTO, BusinessAccountSettingsPresenter> {
		void displayTransactionHistory(List<TransactionDTO> list);

		void displaySubscriptionPlans(List<SubscriptionPlanDTO> plans);

		void setJwtString(String jwt);

		void displayPaymentStatus(String msg, AlertType type);

		void clearPaymentStatus();

		void hideTransactionHistory();

		void disableSubscription();
	}

	private String jwt;
	private AcceptsOneWidget panel;

	@Inject
	public BusinessAccountSettingsActivity(CachingDispatcherAsync dispatcher, EventBus eventBus,
			PlaceController placeController, ApplicationContext ctx,
			IBusinessAccountSettingView view) {
		super(dispatcher, eventBus, placeController, ctx, view);
		setupEventHandler();
	}

	private void setupEventHandler() {
		eventBus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {
			@Override
			public void onEvent(LoginEvent event) {
				internalStart();
			}
		});
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		if (ctx.getAccount() == null) {
			checkLoginStatus();
			return;
		} else {
			internalStart();
		}
	}

	private void internalStart() {
		if (ctx.getAccount() instanceof PersonalAccountDTO) {
			placeController.goTo(new PersonalAccountSettingsPlace());
		}

		bind();
		view.displaySettings((BusinessAccountDTO) ctx.getAccount());
		displayTransactionHistory();
		displaySubscriptionPlans();
		setImageUploadFormSubmitCompleteHandler();
		setUploadFormActionUrl();
		panel.setWidget(view);
	}

	private void displayTransactionHistory() {
		BusinessAccountDTO account = (BusinessAccountDTO) ctx.getAccount();
		for(TransactionDTO txn: account.getTransactions()) {
			if (txn.getStatus() == TransactionStatus.ACTIVE) {
				// disable payment buttons
				view.disableSubscription();
			}
		}
		view.displayTransactionHistory(account.getTransactions());
	}

	private void displaySubscriptionPlans() {
		getJwtString();
		getSubscriptionPlans();
	}

	@Override
	public void bind() {
		view.setPresenter(this);
	}

	private void getSubscriptionPlans() {
		dispatcher.execute(new GetAllSubscriptionPlanAction(),
				new DispatcherCallbackAsync<GetAllSubscriptionPlanResult>() {
					@Override
					public void onSuccess(GetAllSubscriptionPlanResult result) {
						if (result != null) {
							view.displaySubscriptionPlans(result.getPlans());
						}
					}
				});
	}

	@Override
	public void cancel() {
		placeController.goTo(new BusinessAccountPlace());
	}

	@Override
	public void getJwtString() {
		dispatcher.execute(new GetJwtTokenAction(),
				new DispatcherCallbackAsync<GetJwtTokenResult>() {
					@Override
					public void onSuccess(GetJwtTokenResult result) {
						BusinessAccountSettingsActivity.this.jwt = result.getToken();
						view.setJwtString(jwt);
					}
				});
	}

	@Override
	public void pay(final TransactionDTO txn) {
		if (txn == null) {
			throw new IllegalArgumentException();
		}

		dispatcher.execute(new PayAction(txn), new DispatcherCallbackAsync<PayResult>() {
			@Override
			public void onSuccess(PayResult result) {
				if (txn.getStatus() == TransactionStatus.ACTIVE) {
					view.displayPaymentStatus(StringConstants.PAYMENT_SUCCESSFULL,
							AlertType.SUCCESS);
				} else {
					// some day, the sun will shine upon us.
					view.displayPaymentStatus(StringConstants.PAYMENT_UNSUCCESSFULL,
							AlertType.ERROR);
				}

				if (result.getTransaction() != null) {
					BusinessAccountDTO account = (BusinessAccountDTO) ctx.getAccount();
					account.getTransactions().add(result.getTransaction());
					displayTransactionHistory();
				}
			}

			public void onFailure(Throwable th) {
				if (th instanceof DuplicateException) {
					view.displayPaymentStatus(StringConstants.DUPLICATE_SUBSCRIPTION_ATTEMPT, AlertType.ERROR);
				} else {
					view.displayPaymentStatus(StringConstants.PAYMENT_UNSUCCESSFULL, AlertType.ERROR);
				}
			}
		});
	}

	@Override
	public void updatePassword(UpdatePasswordAction action) {
		updatePassword(action, new DispatcherCallbackAsync<UpdatePasswordResult>() {
			@Override
			public void onSuccess(UpdatePasswordResult result) {
				view.displayMessage(StringConstants.PASSWORD_UPDATED, AlertType.SUCCESS);
			}

			public void onFailure(Throwable th) {
				if (th instanceof InvalidCredentialsException) {
					view.displayMessage(th.getMessage(), AlertType.ERROR);
				} else {
					view.displayMessage(StringConstants.PASSWORD_UPDATE_FAILURE, AlertType.ERROR);
				}
			}
		});
	}

	@Override
	public void onProfileLinkClick() {
		placeController.goTo(new BusinessAccountPlace());
	}

	@Override
	public void onInboxLinkClick() {
		placeController.goTo(new ConversationPlace());
	}

	@Override
	public void go(AcceptsOneWidget container) {
	}

	@Override
	public void fetchData() {
	}
}
