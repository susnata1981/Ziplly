package com.ziplly.app.client.view.common.action;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.view.event.TweetSentEvent;
import com.ziplly.app.client.widget.AlertModal;
import com.ziplly.app.shared.TweetResult;

public class TweetHandler extends DispatcherCallbackAsync<TweetResult> {

  public TweetHandler(EventBus eventBus) {
    super(eventBus);
  }

  @Override
  public void onSuccess(TweetResult result) {
//    view.insertTweet(result.getTweet());
    AlertModal modal = new AlertModal();
    modal.showMessage("sucess", AlertType.SUCCESS);
    eventBus.fireEvent(new TweetSentEvent(result.getTweet()));
  }
}
