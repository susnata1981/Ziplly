package com.ziplly.app.client.view.home;

import com.google.gwt.event.shared.EventBus;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.shared.GetNeighborhoodDetailsResult;

public class NeighborhoodDetailsHandler extends DispatcherCallbackAsync<GetNeighborhoodDetailsResult> {
  private HomeView view;

  public NeighborhoodDetailsHandler(EventBus eventBus, HomeView view) {
    super(eventBus);
    this.view = view;
  }

  @Override
  public void onSuccess(GetNeighborhoodDetailsResult result) {
    view.displayCommunitySummaryDetails(result);
  }
}