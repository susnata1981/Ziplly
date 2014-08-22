package com.ziplly.app.client.view.home;

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
import com.ziplly.app.client.places.HomePlace;

public class HomeActivity extends AbstractActivity  {  
  private AcceptsOneWidget panel;
  private HomePlace place;
  private AsyncProvider<HomeViewImpl> viewProvider;
  protected HomePresenterImpl presenter;
  
  @Inject
  public HomeActivity(
      CachingDispatcherAsync dispatcher,
      EventBus eventBus,
      HomePlace place,
      PlaceController placeController,
      ApplicationContext ctx,
      AsyncProvider<HomeViewImpl> viewProvider) {
    super(dispatcher, eventBus, placeController, ctx);
    this.place = place;
    this.viewProvider = viewProvider;
  }

  @Override
  protected void doStart() {
    viewProvider.get(new AsyncCallback<HomeViewImpl>() {
  
      @Override
      public void onFailure(Throwable caught) {
        Window.alert("Failed to load home page");
      }
  
      @Override
      public void onSuccess(HomeViewImpl view) {
        panel.setWidget(view);
        HomeActivity.this.presenter = new HomePresenterImpl(dispatcher, eventBus, place, placeController, ctx, view);
      }
      
    });
  }
  
  @Override
  public String mayStop() {
    return null;
  }

  @Override
  public void onCancel() {
  }

  @Override
  public void onStop() {
    presenter.stop();
  }

  @Override
  public void start(AcceptsOneWidget panel, EventBus eventBus) {
    this.panel = panel;
    checkAccountLogin();
  }
}
