package com.ziplly.app.client.view.common.action;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.ziplly.app.client.resource.StringDefinitions;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.widget.TweetWidget;
import com.ziplly.app.shared.UpdateTweetResult;

public class UpdateTweetHandler extends AbstractClientHandler<UpdateTweetResult> {

  public UpdateTweetHandler(
      EventBus eventBus,
      TweetWidget widget,
      StringDefinitions stringDefinitions) {
    super(eventBus, widget, stringDefinitions);
  }

  @Override
  public void onSuccess(UpdateTweetResult result) {
    widget.updateTweet(result.getTweet());
    widget.displayMessage(StringConstants.TWEET_UPDATED, AlertType.SUCCESS);
  }
}
