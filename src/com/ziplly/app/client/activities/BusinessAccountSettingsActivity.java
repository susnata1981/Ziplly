package com.ziplly.app.client.activities;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
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
import com.ziplly.app.client.view.BusinessAccountSettingsView;
import com.ziplly.app.client.view.BusinessAccountSettingsView.BusinessAccountSettingsPresenter;
import com.ziplly.app.client.view.ISettingsView;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.event.AccountUpdateEvent;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.view.handler.AccountUpdateEventHandler;
import com.ziplly.app.client.view.handler.LoginEventHandler;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.SubscriptionPlanDTO;
import com.ziplly.app.model.SubscriptionPlanStatus;
import com.ziplly.app.model.TransactionDTO;
import com.ziplly.app.model.TransactionStatus;
import com.ziplly.app.shared.GetAllSubscriptionPlanAction;
import com.ziplly.app.shared.GetAllSubscriptionPlanResult;
import com.ziplly.app.shared.GetNeighborhoodAction;
import com.ziplly.app.shared.GetNeighborhoodResult;
import com.ziplly.app.shared.NeighborhoodSearchActionType;
import com.ziplly.app.shared.PayAction;
import com.ziplly.app.shared.PayResult;
import com.ziplly.app.shared.UpdateAccountAction;
import com.ziplly.app.shared.UpdateAccountResult;
import com.ziplly.app.shared.UpdatePasswordAction;
import com.ziplly.app.shared.UpdatePasswordResult;

public class BusinessAccountSettingsActivity extends 
	AbstractAccountSettingsActivity<BusinessAccountDTO, BusinessAccountSettingsActivity.IBusinessAccountSettingView>
		implements BusinessAccountSettingsPresenter {

	public static interface IBusinessAccountSettingView extends
			ISettingsView<BusinessAccountDTO, BusinessAccountSettingsPresenter> {
		void displayTransactionHistory(List<TransactionDTO> list);

		void displaySubscriptionPlans(Map<SubscriptionPlanDTO, String> plans);

		void setJwtString(String jwt);

		void displayPaymentStatus(String msg, AlertType type);

		void clearPaymentStatus();

		void hideTransactionHistory();

		void disableSubscription();

		void displayNeighborhoodListLoading(boolean b);

		void displayNeighborhoods(List<NeighborhoodDTO> neighbordhoods);

		void displayMessageInAddLocationWidget(String accountSaveSuccessful, AlertType success);

		void displayLocationModal(boolean b);
	}

	private AcceptsOneWidget panel;
	private AsyncProvider<BusinessAccountSettingsView> viewProvider;

	@Inject
	public BusinessAccountSettingsActivity(
			CachingDispatcherAsync dispatcher, 
			EventBus eventBus,
			PlaceController placeController, 
			ApplicationContext ctx,
			AsyncProvider<BusinessAccountSettingsView> viewProvider) {
		super(dispatcher, eventBus, placeController, ctx, null);
		this.viewProvider = viewProvider;
		setupEventHandler();
	}

	private void setupEventHandler() {
		super.setupHandlers();
		eventBus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {
			@Override
			public void onEvent(LoginEvent event) {
				ctx.setAccount(event.getAccount());
				internalStart();
			}
		});
		
		eventBus.addHandler(AccountUpdateEvent.TYPE, new AccountUpdateEventHandler() {

			@Override
			public void onEvent(AccountUpdateEvent event) {
				view.displaySettings((BusinessAccountDTO) ctx.getAccount());
			}
			
		});
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		checkAccountLogin();
	}

	@Override
	public void doStart() {
		internalStart();
	}
	
	private void internalStart() {
		if (ctx.getAccount() instanceof PersonalAccountDTO) {
			placeController.goTo(new PersonalAccountSettingsPlace());
			return;
		}

		viewProvider.get(new DefaultViewLoaderAsyncCallback<BusinessAccountSettingsView>() {

			@Override
			public void onSuccess(BusinessAccountSettingsView result) {
				BusinessAccountSettingsActivity.this.view = result;
				bind();
				view.displaySettings((BusinessAccountDTO) ctx.getAccount());
				displayTransactionHistory();
				displaySubscriptionPlans();
				setImageUploadFormSubmitCompleteHandler();
				setUploadFormActionUrl();
				panel.setWidget(view);
			}
		});
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
//		getJwtString();
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
							boolean introductoryPeriod = true;
							Map<SubscriptionPlanDTO, String> plans = sortSubscriptionPlans(result.getPlans());
							view.displaySubscriptionPlans(result.getPlans());
							for(SubscriptionPlanDTO plan : plans.keySet()) {
								if (plan.getStatus() != SubscriptionPlanStatus.DISABLED) {
									introductoryPeriod = false;
								}
							}
							
							// If none of the plans are active
							if (introductoryPeriod) {
								view.displayPaymentStatus(StringConstants.FREE_INTRODUCTORY_PLAN, AlertType.INFO);
							}
						}
					}
				});
	}

	private Map<SubscriptionPlanDTO, String> sortSubscriptionPlans(Map<SubscriptionPlanDTO, String> plans) {
		TreeMap<SubscriptionPlanDTO, String> sortedPlans = new TreeMap<SubscriptionPlanDTO, String>(
		    new Comparator<SubscriptionPlanDTO>() {
			@Override
			public int compare(SubscriptionPlanDTO o1, SubscriptionPlanDTO o2) {
				return (int)(o1.getFee() - o2.getFee());
			}
		});
		sortedPlans.putAll(plans);
		return sortedPlans;
	}
	
	@Override
	public void cancel() {
		placeController.goTo(new BusinessAccountPlace());
	}

//	@Override
//	public void getJwtString() {
//		dispatcher.execute(new GetJwtTokenAction(),
//				new DispatcherCallbackAsync<GetJwtTokenResult>() {
//					@Override
//					public void onSuccess(GetJwtTokenResult result) {
//						BusinessAccountSettingsActivity.this.jwt = result.getToken();
//						view.setJwtString(jwt);
//					}
//				});
//	}

	@Override
	public void pay(final TransactionDTO txn) {
		if (txn == null) {
			throw new IllegalArgumentException();
		}

		if (txn.getStatus() != TransactionStatus.ACTIVE) {
			return;
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
	public void getNeighborhoodData(String postalCode) {
		view.displayNeighborhoodListLoading(true);
		GetNeighborhoodAction action = new GetNeighborhoodAction(postalCode);
		action.setSearchType(NeighborhoodSearchActionType.BY_ZIP);
		dispatcher.execute(action, new NeighborhoodHandler());
	}
	
	@Override
	public void go(AcceptsOneWidget container) {
	}
	
	public class NeighborhoodHandler extends DispatcherCallbackAsync<GetNeighborhoodResult> {
		@Override
		public void onSuccess(GetNeighborhoodResult result) {
			if (result.getNeighbordhoods() != null && result.getNeighbordhoods().size() > 0) {
				view.displayNeighborhoods(result.getNeighbordhoods());
			} else {
				view.displayMessageInAddLocationWidget(StringConstants.NOT_AVAILABLE_IN_AREA, AlertType.INFO);
			}
			view.displayNeighborhoodListLoading(false);
		}
	}

	@Override
	public void updateLocation(BusinessAccountDTO account) {
		if (account == null) {
			throw new IllegalArgumentException();
		}
		dispatcher.execute(new UpdateAccountAction(account), new DispatcherCallbackAsync<UpdateAccountResult>() {
			@Override
			public void onSuccess(UpdateAccountResult result) {
				// Fire event.
				view.displayMessageInAddLocationWidget(StringConstants.ACCOUNT_SAVE_SUCCESSFUL, AlertType.SUCCESS);

				// Update account and fire event.
				ctx.setAccount(result.getAccount());
				eventBus.fireEvent(new AccountUpdateEvent(result.getAccount()));
				view.displayLocationModal(false);
			}
			
			@Override
			public void onFailure(Throwable th) {
				view.displayMessageInAddLocationWidget(StringConstants.FAILED_TO_SAVE_ACCOUNT, AlertType.ERROR);
			}
		});
	}
}
