package com.ziplly.app.client.view.account;

import com.google.gwt.event.shared.EventBus;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.view.common.RenderingStatusCallback;
import com.ziplly.app.client.view.event.LoadingEventEnd;
import com.ziplly.app.client.view.event.TweetNotAvailableEvent;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.shared.GetTweetForUserResult;

public class GetTweetForUserHandler<T extends AccountDTO> extends DispatcherCallbackAsync<GetTweetForUserResult> {
  private IAccountView<T> view;
  private BaseAccountViewPresenter<T> presenter;
  private boolean displayMessageIfNoTweetsPresent;

  public GetTweetForUserHandler(EventBus eventBus, IAccountView<T> view, BaseAccountViewPresenter<T> presenter,
      boolean displayMessageIfNoTweetsPresent) {
    super(eventBus);
    this.view = view;
    this.presenter = presenter;
    this.displayMessageIfNoTweetsPresent = displayMessageIfNoTweetsPresent;
  }
  
  @Override
  public void onSuccess(GetTweetForUserResult result) {
    presenter.setLastFetchedTweets(result.getTweets());
    view.displayTweets(result.getTweets(), displayMessageIfNoTweetsPresent, new RenderingStatusCallback() {
      
      @Override
      public void finished(double completedPercentage) {
        if (completedPercentage == 100) {
          
          if (!presenter.isScrollThreadStarted()) {
            presenter.startInfiniteScrollThread();
          }
          eventBus.fireEvent(new LoadingEventEnd());
        }
      }
    });
  }

  @Override
  public void onFailure(Throwable th) {
    super.onFailure(th);
//    presenter.stopThreads();
    eventBus.fireEvent(new LoadingEventEnd());
    eventBus.fireEvent(new TweetNotAvailableEvent());
  }
}
