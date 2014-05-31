package com.ziplly.app.client.view.coupon;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.ziplly.app.client.view.AbstractView;
import com.ziplly.app.client.view.TweetView;
import com.ziplly.app.client.view.event.LoadingEventEnd;
import com.ziplly.app.model.TweetDTO;

public class CouponExplorerView extends AbstractView {

  private static CouponExplorerViewUiBinder uiBinder = GWT.create(CouponExplorerViewUiBinder.class);

  interface CouponExplorerViewUiBinder extends UiBinder<Widget, CouponExplorerView> {
  }

  @Inject
  public CouponExplorerView(EventBus eventBus) {
    super(eventBus);
    initWidget(uiBinder.createAndBindUi(this));
  }

  @UiField
  TweetView tweetView;
  
  public void display(List<TweetDTO> tweets) {
    tweetView.clear();

    if (tweets.size() == 0) {
      eventBus.fireEvent(new LoadingEventEnd());
      return;
    }

    tweetView.displayTweets(tweets);
  }

  public TweetView getTweetView() {
    return tweetView;
  }
}
