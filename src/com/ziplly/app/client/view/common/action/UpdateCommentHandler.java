package com.ziplly.app.client.view.common.action;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.ziplly.app.client.resource.StringDefinitions;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.widget.TweetWidget;
import com.ziplly.app.shared.UpdateCommentResult;

public class UpdateCommentHandler extends AbstractClientHandler<UpdateCommentResult> {

  public UpdateCommentHandler(EventBus eventBus,
      TweetWidget widget,
      StringDefinitions stringDefinitions) {
    super(eventBus, widget, stringDefinitions);
  }

  @Override
  public void onSuccess(UpdateCommentResult result) {
    widget.displayMessage(StringConstants.COMMENT_UPDATED, AlertType.SUCCESS);
    widget.updateComment(result.getComment());
  }
}
