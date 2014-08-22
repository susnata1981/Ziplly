package com.ziplly.app.client.view.common.action;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.ziplly.app.client.resource.StringDefinitions;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.widget.TweetWidget;
import com.ziplly.app.shared.ReportSpamResult;

public class ReportSpamHandler extends AbstractClientHandler<ReportSpamResult> {
  
  public ReportSpamHandler(EventBus eventBus,
      TweetWidget widget,
      StringDefinitions stringDefinitions) {
    super(eventBus, widget, stringDefinitions);
  }

  @Override
  public void onSuccess(ReportSpamResult result) {
    widget.displayMessage(StringConstants.REPORT_SPAM_SUCCESSFUL, AlertType.SUCCESS);
  }
}