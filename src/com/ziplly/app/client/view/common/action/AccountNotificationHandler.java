package com.ziplly.app.client.view.common.action;

import com.google.gwt.event.shared.EventBus;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.view.event.AccountNotificationEvent;
import com.ziplly.app.shared.GetAccountNotificationResult;

public class AccountNotificationHandler extends DispatcherCallbackAsync<GetAccountNotificationResult> {

  public AccountNotificationHandler(EventBus eventBus) {
    super(eventBus);
    assert(eventBus != null);
  }

  @Override
  public void onSuccess(GetAccountNotificationResult result) {
    eventBus.fireEvent(new AccountNotificationEvent(result.getAccountNotifications()));
  }
}
