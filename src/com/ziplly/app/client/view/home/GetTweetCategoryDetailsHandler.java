package com.ziplly.app.client.view.home;

import com.google.gwt.event.shared.EventBus;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.shared.GetTweetCategoryDetailsResult;

public class GetTweetCategoryDetailsHandler extends DispatcherCallbackAsync<GetTweetCategoryDetailsResult> {
  private HomeView view;
  
  public GetTweetCategoryDetailsHandler(EventBus eventBus, HomeView view) {
    super(eventBus);
    this.view = view;
  }
  
  @Override
  public void onSuccess(GetTweetCategoryDetailsResult result) {
    view.updateTweetCategoryCount(result.getTweetCounts());
  }
}
