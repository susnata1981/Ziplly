package com.ziplly.app.client.activities;

import java.util.List;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.common.CommonUtil;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.exceptions.InvalidCredentialsException;
import com.ziplly.app.client.places.BusinessAccountPlace;
import com.ziplly.app.client.places.BusinessAccountSettingsPlace;
import com.ziplly.app.client.places.BusinessAccountSettingsPlace.SettingsTab;
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
import com.ziplly.app.client.widget.ConfirmationModalWidget;
import com.ziplly.app.client.widget.ConfirmationModalWidget.ConfirmationModalCallback;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.SubscriptionPlanDTO;
import com.ziplly.app.model.overlay.SubscriptionDTO;
import com.ziplly.app.shared.CheckSubscriptionEligibilityAction;
import com.ziplly.app.shared.CheckSubscriptionEligibilityResult;
import com.ziplly.app.shared.GetAllSubscriptionPlanAction;
import com.ziplly.app.shared.GetAllSubscriptionPlanResult;
import com.ziplly.app.shared.SubscriptionEligibilityStatus;
import com.ziplly.app.shared.UpdatePasswordAction;
import com.ziplly.app.shared.UpdatePasswordResult;

public class BusinessAccountSettingsActivity extends
    AbstractAccountSettingsActivity<BusinessAccountDTO, BusinessAccountSettingsActivity.IBusinessAccountSettingView> implements
    BusinessAccountSettingsPresenter {

	public static interface IBusinessAccountSettingView extends
	    ISettingsView<BusinessAccountDTO, BusinessAccountSettingsPresenter> {
		void displayTransactionHistory(List<SubscriptionDTO> subscriptions);

		void displaySubscriptionPlans(List<SubscriptionPlanDTO> plans);

		void displayPaymentStatus(String msg, AlertType type);

		void clearPaymentStatus();

		void hideTransactionHistory();

		void initiatePay(Long subscriptionId, String token);

    void setTab(SettingsTab tab);
	}

	private AcceptsOneWidget panel;
	private AsyncProvider<BusinessAccountSettingsView> viewProvider;
  private BusinessAccountSettingsPlace place;

	@Inject
	public BusinessAccountSettingsActivity(CachingDispatcherAsync dispatcher,
	    EventBus eventBus,
	    PlaceController placeController,
	    ApplicationContext ctx,
	    AsyncProvider<BusinessAccountSettingsView> viewProvider,
	    BusinessAccountSettingsPlace place) {
		super(dispatcher, eventBus, placeController, ctx, null);
		this.viewProvider = viewProvider;
		this.place = place;
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
				view.setTab(place.getTab());
				panel.setWidget(view);
			}
		});
	}

	private void displayTransactionHistory() {
		BusinessAccountDTO account = (BusinessAccountDTO) ctx.getAccount();
		CommonUtil.sort(account.getTransactions());
		view.displayTransactionHistory(account.getTransactions());
	}

	private void displaySubscriptionPlans() {
		dispatcher.execute(
		    new GetAllSubscriptionPlanAction(),
		    new DispatcherCallbackAsync<GetAllSubscriptionPlanResult>() {
			    @Override
			    public void onSuccess(GetAllSubscriptionPlanResult result) {
				    if (result != null) {
					    view.displaySubscriptionPlans(result.getSubscriptionPlans());
				    }
			    }
		    });
	}

	@Override
	public void bind() {
		view.setPresenter(this);
	}

	@Override
	public void cancel() {
		placeController.goTo(new BusinessAccountPlace());
	}

	@Override
	public void updatePassword(UpdatePasswordAction action) {
		updatePassword(action, new DispatcherCallbackAsync<UpdatePasswordResult>() {
			@Override
			public void onSuccess(UpdatePasswordResult result) {
				view.displayMessage(StringConstants.PASSWORD_UPDATED, AlertType.SUCCESS);
			}

			@Override
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
  public void checkSubscriptionEligibility(final Long subscriptionId) {
		CheckSubscriptionEligibilityAction action =  new CheckSubscriptionEligibilityAction();
		action.setSubscriptionId(subscriptionId);
		dispatcher.execute(action, new DispatcherCallbackAsync<CheckSubscriptionEligibilityResult>() {

			@Override
      public void onSuccess(final CheckSubscriptionEligibilityResult result) {
				if (result.getEligibilityStatus() == SubscriptionEligibilityStatus.ELIGIBLE) {
					view.initiatePay(subscriptionId, result.getToken());
				} 
				else if (result.getEligibilityStatus() == SubscriptionEligibilityStatus.ACTIVE_SUBSCRIPTION) {
					@SuppressWarnings("unused")
          final ConfirmationModalWidget modal = new ConfirmationModalWidget(stringDefinitions.cancelledSubscriptionStillActive(),
              new ConfirmationModalCallback() {
						
						@Override
						public void confirm() {
							view.initiatePay(subscriptionId, result.getToken());
						}
						
						@Override
						public void cancel() {
						}
					});
				} 
				else {
					view.displayMessage(stringDefinitions.ineglibleForSubscription(), AlertType.WARNING);
				}
      }
		});
  }
	
	@Override
  public void go(AcceptsOneWidget container) {
  }
}
