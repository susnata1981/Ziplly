package com.ziplly.app.client.view.home;

import com.google.gwt.event.shared.EventBus;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.shared.GetHashtagResult;

public class GetHashTagHandler extends DispatcherCallbackAsync<GetHashtagResult> {
  private HomeView view;

  public GetHashTagHandler(EventBus eventBus, HomeView view) {
    super(eventBus);
    this.view = view;
  }
  
  @Override
  public void onSuccess(GetHashtagResult result) {
    view.displayHashtag(result.getHashtags());
  }
}
