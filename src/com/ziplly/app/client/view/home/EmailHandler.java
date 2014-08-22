package com.ziplly.app.client.view.home;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.shared.EmailAdminResult;

public class EmailHandler extends DispatcherCallbackAsync<EmailAdminResult> {
  private HomeView view;

  public EmailHandler(EventBus eventBus, HomeView view) {
    super(eventBus);
    this.view = view;
  }
  
  @Override
  public void onSuccess(EmailAdminResult result) {
    view.displayMessage(StringConstants.FEEDBACK_SENT_SUCCESS, AlertType.SUCCESS);
  }
}
