package com.ziplly.app.client.view.account;

import com.google.gwt.event.shared.EventBus;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.shared.GetTweetForUserResult;

@Deprecated
public class ScrollBottomHitActionHandler<T extends AccountDTO> extends DispatcherCallbackAsync<GetTweetForUserResult> {
  private IAccountView<T> view;
  private BaseAccountViewPresenter<T> presenter;

  public ScrollBottomHitActionHandler(EventBus eventBus, IAccountView<T> view, BaseAccountViewPresenter<T> presenter) {
    super(eventBus);
    this.view = view;
    this.presenter = presenter;
  }
  
  @Override
  public void onSuccess(GetTweetForUserResult result) {
//    eventBus.fireEvent(new LoadingEventStart());
    presenter.setLastFetchedTweets(result.getTweets());
    view.addTweets(result.getTweets());
  }
}