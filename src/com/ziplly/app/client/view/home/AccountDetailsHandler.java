package com.ziplly.app.client.view.home;

import com.google.gwt.event.shared.EventBus;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.view.event.AccountDetailsUpdateEvent;
import com.ziplly.app.shared.GetAccountDetailsResult;

public class AccountDetailsHandler extends DispatcherCallbackAsync<GetAccountDetailsResult> {
  private EventBus eventBus;
  private HomePresenter homePresenter;

  public AccountDetailsHandler(HomePresenter homePresenter, EventBus eventBus) {
    super(eventBus);
    this.eventBus = eventBus;
    this.homePresenter = homePresenter;
  }
  
  @Override
  public void onSuccess(GetAccountDetailsResult result) {
    eventBus.fireEvent(new AccountDetailsUpdateEvent(result));
//    homePresenter.displayCommunityWall();
  }
}
