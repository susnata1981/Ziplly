package com.ziplly.app.client.view.account;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.common.CommonUtil;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.BusinessAccountSettingsPlace;
import com.ziplly.app.client.widget.ConfirmationModalWidget;
import com.ziplly.app.client.widget.ConfirmationModalWidget.ConfirmationModalCallback;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.shared.CheckSubscriptionEligibilityAction;
import com.ziplly.app.shared.CheckSubscriptionEligibilityResult;
import com.ziplly.app.shared.GetAllSubscriptionPlanAction;
import com.ziplly.app.shared.GetAllSubscriptionPlanResult;
import com.ziplly.app.shared.SubscriptionEligibilityStatus;

public class BusinessAccountSettingsPresenterImpl extends AccountSettingPresenterImpl<BusinessAccountDTO, BusinessAccountSettingsPresenter> 
  implements BusinessAccountSettingsPresenter {

  private BusinessAccountSettingsPlace place;

  public BusinessAccountSettingsPresenterImpl(
      CachingDispatcherAsync dispatcher,
      EventBus eventBus,
      PlaceController placeController,
      ApplicationContext ctx,
      AcceptsOneWidget panel,
      BusinessAccountSettingsView view,
      BusinessAccountSettingsPlace place) {
    super(dispatcher, eventBus, placeController, ctx, panel, view);
    this.place = place;
    start();
  }

  private void start() {
    view.displaySettings((BusinessAccountDTO) ctx.getAccount());
    displayTransactionHistory();
    displaySubscriptionPlans();
    setImageUploadFormSubmitCompleteHandler();
    setUploadFormActionUrl();
    ((BusinessAccountSettingsView)view).setTab(place.getTab());
  }

  private void displayTransactionHistory() {
    BusinessAccountDTO account = (BusinessAccountDTO) ctx.getAccount();
    CommonUtil.sort(account.getTransactions());
    ((BusinessAccountSettingsView)view).displayTransactionHistory(account.getTransactions());
  }

  private void displaySubscriptionPlans() {
    dispatcher.execute(
        new GetAllSubscriptionPlanAction(),
        new DispatcherCallbackAsync<GetAllSubscriptionPlanResult>(eventBus) {
          @Override
          public void onSuccess(GetAllSubscriptionPlanResult result) {
            if (result != null) {
              ((BusinessAccountSettingsView)view).displaySubscriptionPlans(result.getSubscriptionPlans());
            }
          }
        });
  }

  
  public void checkSubscriptionEligibility(final Long subscriptionId) {
    CheckSubscriptionEligibilityAction action =  new CheckSubscriptionEligibilityAction();
    action.setSubscriptionId(subscriptionId);
    
    dispatcher.execute(action, new DispatcherCallbackAsync<CheckSubscriptionEligibilityResult>(eventBus) {

      @Override
      public void onSuccess(final CheckSubscriptionEligibilityResult result) {
        if (result.getEligibilityStatus() == SubscriptionEligibilityStatus.ELIGIBLE) {
          ((BusinessAccountSettingsView)view).initiatePay(subscriptionId, result.getToken());
        } 
        else if (result.getEligibilityStatus() == SubscriptionEligibilityStatus.ACTIVE_SUBSCRIPTION) {
          
          @SuppressWarnings("unused")
          final ConfirmationModalWidget modal = new ConfirmationModalWidget(
              ApplicationContext.getStringDefinitions().cancelledSubscriptionStillActive(),
              new ConfirmationModalCallback() {
            
            @Override
            public void confirm() {
              ((BusinessAccountSettingsView)view).initiatePay(subscriptionId, result.getToken());
            }
            
            @Override
            public void cancel() {
            }
          });
        } 
        else {
          view.displayMessage(ApplicationContext.getStringDefinitions().ineglibleForSubscription(), 
              AlertType.WARNING);
        }
      }
    });
  }
  
  @Override
  public void bind() {
    view.setPresenter(this);
  }
}
