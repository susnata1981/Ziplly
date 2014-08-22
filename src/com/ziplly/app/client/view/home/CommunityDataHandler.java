package com.ziplly.app.client.view.home;

import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.view.common.RenderingStatusCallback;
import com.ziplly.app.client.view.event.LoadingEventEnd;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.shared.GetCommunityWallDataResult;

public class CommunityDataHandler extends DispatcherCallbackAsync<GetCommunityWallDataResult> {
  private HomeViewState state;
  private EventBus eventBus;
  private HomeView view;
  private boolean resetTweets;

  public CommunityDataHandler(EventBus eventBus, HomeViewState state, HomeView view, boolean resetTweets) {
    super(eventBus);
    this.state = state;
    this.eventBus = eventBus;
    this.view = view;
    this.resetTweets = resetTweets;
  }

  @Override
  public void onSuccess(GetCommunityWallDataResult result) {
    List<TweetDTO> tweets = result.getTweets();
    state.setCurrentTweetList(tweets);
    state.setFetchingData(false);
    
    if (resetTweets) {
      view.resetTweets();
    }
    
    view.insertLast(tweets, new RenderingStatusCallback() {

      @Override
      public void finished(double completedPercentage) {
        if (completedPercentage == 100) {
          eventBus.fireEvent(new LoadingEventEnd());
        }
      }

    });
  }
}
