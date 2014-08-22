package com.ziplly.app.client.view.common.action;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.ziplly.app.client.resource.StringDefinitions;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.widget.TweetWidget;
import com.ziplly.app.shared.CommentResult;

public class PostCommentHandler extends AbstractClientHandler<CommentResult> {

  public PostCommentHandler(EventBus eventBus, TweetWidget widget, StringDefinitions stringDefinitions) {
    super(eventBus, widget, stringDefinitions);
  }
  
  @Override
  public void onSuccess(CommentResult result) {
    widget.addComment(result.getComment());
    widget.displayMessage(StringConstants.COMMENT_UPDATED, AlertType.SUCCESS);
  }
}