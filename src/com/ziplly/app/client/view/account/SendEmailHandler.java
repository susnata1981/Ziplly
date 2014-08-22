package com.ziplly.app.client.view.account;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.shared.SendEmailResult;

public class SendEmailHandler<T extends AccountDTO> extends DispatcherCallbackAsync<SendEmailResult> {
  private IAccountView<T> view;

  public SendEmailHandler(EventBus eventBus, IAccountView<T> view) {
    super(eventBus);
    this.view = view;
  }

  @Override
  public void onSuccess(SendEmailResult result) {
    view.displayModalMessage(StringConstants.EMAIL_SENT, AlertType.SUCCESS);
  }
}
