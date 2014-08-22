package com.ziplly.app.client.view.home;

import com.google.gwt.event.shared.EventBus;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.shared.GetImageUploadUrlResult;

public class GetImageUploadHandler extends DispatcherCallbackAsync<GetImageUploadUrlResult> {
  private HomeView view;

  public GetImageUploadHandler(EventBus eventBus, HomeView view) {
    super(eventBus);
    this.view = view;
  }
  
  @Override
  public void onSuccess(GetImageUploadUrlResult result) {
    System.out.println("Setting upload form action url to "+result.getImageUrl());
    view.setImageUploadUrl(result.getImageUrl());
  }
}
