package com.ziplly.app.client.view.account;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.web.bindery.event.shared.EventBus;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.exceptions.InvalidCredentialsException;
import com.ziplly.app.client.view.ISettingsView;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.shared.UpdatePasswordResult;

public class UpdatePasswordHandler extends DispatcherCallbackAsync<UpdatePasswordResult> {
  private ISettingsView<?,?> view;

  public UpdatePasswordHandler(EventBus eventBus, ISettingsView<?,?> view) {
    super(eventBus);
    this.view = view;
  }

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
}
