package com.ziplly.app.client.view.home;

import com.google.gwt.event.shared.EventBus;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.shared.GetNewMemberResult;

public class GetNewMemberHandler extends DispatcherCallbackAsync<GetNewMemberResult> {
  private HomeView view;

  public GetNewMemberHandler(EventBus eventBus, HomeView view) {
    super(eventBus);
    this.view = view;
  }
  
  @Override
  public void onSuccess(GetNewMemberResult result) {
    view.displayNewMembers(result.getAccounts());
    view.resizeMap();
  }
}
