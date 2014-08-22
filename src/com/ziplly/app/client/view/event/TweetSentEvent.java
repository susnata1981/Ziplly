package com.ziplly.app.client.view.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.ziplly.app.model.TweetDTO;

public class TweetSentEvent extends GwtEvent<TweetSentEvent.Handler>{
  public static Type<Handler> TYPE = new Type<Handler>();
  private TweetDTO tweet;
  
  public TweetSentEvent(TweetDTO tweet) {
    this.tweet = tweet;
  }
  
  @Override
  public com.google.gwt.event.shared.GwtEvent.Type<Handler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(Handler handler) {
    handler.onEvent(this);
  }
  
  public TweetDTO getTweet() {
    return tweet;
  }

  public interface Handler extends EventHandler {
    public void onEvent(TweetSentEvent event);
  }
}
