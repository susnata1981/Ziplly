package com.ziplly.app.client.view.coupon;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.activities.AbstractActivity;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.CouponExplorerPlace;
import com.ziplly.app.client.view.event.LoadingEventEnd;
import com.ziplly.app.client.view.event.LoadingEventStart;
import com.ziplly.app.shared.GetCommunityWallDataAction;
import com.ziplly.app.shared.GetCommunityWallDataAction.SearchType;
import com.ziplly.app.shared.GetCommunityWallDataResult;

public class CouponExplorerActivity extends AbstractActivity {
  private CouponExplorerView view;
  private AsyncProvider<CouponExplorerView> viewProvider;
  private AcceptsOneWidget panel;
  private CouponExplorerPlace place;
  private TweetPresenterAdapter tweetPresenterAdapter;
  
  @Inject
  public CouponExplorerActivity(
      CachingDispatcherAsync dispatcher,
      EventBus eventBus,
      PlaceController placeController,
      CouponExplorerPlace place,
      ApplicationContext ctx,
      AsyncProvider<CouponExplorerView> viewProvider) {
    super(dispatcher, eventBus, placeController, ctx);
    this.place = place;
    this.viewProvider = viewProvider;
    tweetPresenterAdapter = new TweetPresenterAdapter(placeController);
  }

  @Override
  public void start(final AcceptsOneWidget panel, EventBus eventBus) {
    this.panel = panel;
    viewProvider.get(new AsyncCallback<CouponExplorerView>() {

      @Override
      public void onFailure(Throwable caught) {
        Window.alert("Sorry having issues with network connection");
      }

      @Override
      public void onSuccess(CouponExplorerView result) {
        CouponExplorerActivity.this.view = result;
        bind();
        panel.setWidget(result);
        loadCouponData();
      }
     
    });
  }

  private void loadCouponData() {
    eventBus.fireEvent(new LoadingEventStart());
    GetCommunityWallDataAction action = new GetCommunityWallDataAction();
    action.setSearchType(SearchType.COUPONS);
    action.setPage(0);
    action.setPageSize(Integer.MAX_VALUE);
    dispatcher.execute(action, new DispatcherCallbackAsync<GetCommunityWallDataResult>() {

      @Override
      public void onSuccess(GetCommunityWallDataResult result) {
        view.display(result.getTweets());
        eventBus.fireEvent(new LoadingEventEnd());
      }
    });
  }

  private void bind() {
    view.getTweetView().setPresenter(tweetPresenterAdapter);
  }
  

  @Override
  protected void doStart() {
    // Do nothing.
  }
}
