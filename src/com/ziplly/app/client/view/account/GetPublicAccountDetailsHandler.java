package com.ziplly.app.client.view.account;

import com.google.gwt.event.shared.EventBus;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.shared.GetAccountDetailsResult;

public class GetPublicAccountDetailsHandler extends DispatcherCallbackAsync<GetAccountDetailsResult> {
  private IAccountView<?> view;

  public GetPublicAccountDetailsHandler(EventBus eventBus, IAccountView<?> view) {
    super(eventBus);
    this.view = view;
  }

  @Override
  public void onSuccess(GetAccountDetailsResult result) {
    view.updatePublicAccountDetails(result);
  }
}
