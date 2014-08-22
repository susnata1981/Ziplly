package com.ziplly.app.client.view.account;

import com.google.gwt.event.shared.EventBus;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.shared.GetAccountDetailsResult;

public class GetAccountDetailsHandler<T extends AccountDTO> extends
    DispatcherCallbackAsync<GetAccountDetailsResult> {
  private IAccountView<T> view;
  private ApplicationContext ctx;

  public GetAccountDetailsHandler(EventBus eventBus, IAccountView<T> view, ApplicationContext ctx) {
    super(eventBus);
    this.view = view;
    this.ctx = ctx;
  }

  @Override
  public void onSuccess(GetAccountDetailsResult result) {
    ctx.setAccountDetails(result);
    // TODO : change dependency on applicaiton context.
    view.updateAccountDetails(ctx.getAccountDetails());
  }
}
