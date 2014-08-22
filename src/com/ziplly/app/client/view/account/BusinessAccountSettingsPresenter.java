package com.ziplly.app.client.view.account;

import com.ziplly.app.client.activities.AccountSettingsPresenter;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.model.BusinessAccountDTO;

public interface BusinessAccountSettingsPresenter extends AccountSettingsPresenter<BusinessAccountDTO>, Presenter {

  public void checkSubscriptionEligibility(final Long subscriptionId);
  
}
