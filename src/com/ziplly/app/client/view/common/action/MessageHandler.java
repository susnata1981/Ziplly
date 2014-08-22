package com.ziplly.app.client.view.common.action;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.resource.StringDefinitions;
import com.ziplly.app.client.widget.AlertModal;
import com.ziplly.app.shared.SendMessageResult;

public class MessageHandler extends DispatcherCallbackAsync<SendMessageResult> {
  private StringDefinitions stringDefinitions;

  public MessageHandler(EventBus eventBus, StringDefinitions stringDefinitions) {
    super(eventBus);
    this.stringDefinitions = stringDefinitions;
  }

  @Override
  public void onSuccess(SendMessageResult result) {
    AlertModal modal = new AlertModal();
    modal.showMessage(stringDefinitions.messageSent(), AlertType.SUCCESS);
  }
}
