package com.ziplly.app.client.view.common.action;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.ziplly.app.client.resource.StringDefinitions;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.widget.TweetWidget;
import com.ziplly.app.shared.DeleteTweetResult;

public class DeleteTweetHandler extends AbstractClientHandler<DeleteTweetResult> {
  
  public DeleteTweetHandler(
      EventBus eventBus,
      TweetWidget widget,
      StringDefinitions stringDefinitions) {
    super(eventBus, widget, stringDefinitions);
  }

  @Override
  public void onSuccess(DeleteTweetResult result) {
    widget.displayMessage(StringConstants.TWEET_REMOVED, AlertType.SUCCESS);
    widget.removeFromParent();
  }
}