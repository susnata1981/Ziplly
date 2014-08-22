package com.ziplly.app.client.view.account;

import com.google.web.bindery.event.shared.EventBus;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.view.ISettingsView;
import com.ziplly.app.shared.GetImageUploadUrlResult;

public class GetImageUrlUploadHandler extends DispatcherCallbackAsync<GetImageUploadUrlResult> {
  private ISettingsView<?,?> view;

  public GetImageUrlUploadHandler(EventBus eventBus, ISettingsView<?,?> view) {
    super(eventBus);
    this.view = view;
  }

  @Override
  public void onSuccess(GetImageUploadUrlResult result) {
    // TODO hack for making it work in local environment
    String url = result.getImageUrl().replace("susnatas-MacBook-Pro.local:8888", "127.0.0.1:8888");
    System.out.println("Setting upload image form action to:" + url);
    view.setUploadFormActionUrl(url);
  }
}