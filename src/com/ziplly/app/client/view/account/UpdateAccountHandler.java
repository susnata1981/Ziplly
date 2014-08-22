package com.ziplly.app.client.view.account;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.web.bindery.event.shared.EventBus;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.view.ISettingsView;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.event.AccountUpdateEvent;
import com.ziplly.app.client.view.event.LoadingEventEnd;
import com.ziplly.app.shared.UpdateAccountResult;

public class UpdateAccountHandler extends DispatcherCallbackAsync<UpdateAccountResult> {
  private ISettingsView<?, ?> view;
  private ApplicationContext ctx;

  public UpdateAccountHandler(EventBus eventBus, ISettingsView<?,?> view, ApplicationContext ctx) {
    super(eventBus);
    this.view = view;
    this.ctx = ctx;
  }

  @Override
  public void onSuccess(UpdateAccountResult result) {
    // Fire event.
    view.displayMessage(ApplicationContext.getStringDefinitions().accountUpdated(), AlertType.SUCCESS);
    ctx.setAccount(result.getAccount());
    // Update account and fire event.
    eventBus.fireEvent(new AccountUpdateEvent(result.getAccount()));
    view.showSaveButton(true);
    eventBus.fireEvent(new LoadingEventEnd());
  }

  @Override
  public void onFailure(Throwable th) {
    view.displayMessage(StringConstants.FAILED_TO_SAVE_ACCOUNT, AlertType.ERROR);
    view.showSaveButton(true);
    eventBus.fireEvent(new LoadingEventEnd());
  }
}
