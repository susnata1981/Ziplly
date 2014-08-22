package com.ziplly.app.client.view.common;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.resource.StringDefinitions;
import com.ziplly.app.client.view.common.action.TweetHandler;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.shared.TweetAction;

public class TweetBoxPresenterImpl extends BasePresenter implements TweetBoxPresenter {
  private TweetHandler tweetHandler;
  
  @Inject
  public TweetBoxPresenterImpl(
      CachingDispatcherAsync dispatcher,
      EventBus eventBus,
      StringDefinitions stringDefinitions,
      ApplicationContext ctx) {
    super(dispatcher, eventBus, stringDefinitions, ctx);
  }

  @Override
  public void sendTweet(TweetDTO tweet) {
    if (tweetHandler == null) {
      tweetHandler = new TweetHandler(eventBus);
    }
    
    dispatcher.execute(new TweetAction(tweet), tweetHandler);
  }

  @Override
  public void deleteImage(String url) {
    // TODO Auto-generated method stub
//            dispatcher.execute(new DeleteTweetAction(tweet.getTweetId()), new
//                   DeleteTweetHandler(eventBus, tweet)); }

  }
}
