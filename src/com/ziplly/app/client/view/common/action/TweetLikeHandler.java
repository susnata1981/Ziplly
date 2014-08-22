package com.ziplly.app.client.view.common.action;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.ziplly.app.client.resource.StringDefinitions;
import com.ziplly.app.client.widget.TweetWidget;
import com.ziplly.app.shared.LikeResult;

public class TweetLikeHandler extends AbstractClientHandler<LikeResult> {

  public TweetLikeHandler(EventBus eventBus, TweetWidget widget, StringDefinitions stringDefinitions) {
    super(eventBus, widget, stringDefinitions);
  }
  
  @Override
  public void onSuccess(LikeResult result) {
    widget.updateLike(result.getLike());
    widget.displayMessage(stringDefinitions.likeSaved(), AlertType.SUCCESS);
  }
}
