package com.ziplly.app.client.activities;

import java.util.Date;
import java.util.List;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.exceptions.ErrorDefinitions;
import com.ziplly.app.client.exceptions.NeedsSubscriptionException;
import com.ziplly.app.client.places.CouponReportPlace;
import com.ziplly.app.client.view.View;
import com.ziplly.app.client.view.coupon.CouponFormWidget;
import com.ziplly.app.client.view.coupon.CouponReportViewImpl.CouponReportPresenter;
import com.ziplly.app.client.view.coupon.CouponTransactionSummaryCalculator;
import com.ziplly.app.client.view.coupon.TransactionSummary;
import com.ziplly.app.client.view.event.CouponPublishSuccessfulEvent;
import com.ziplly.app.client.view.event.LoadingEventEnd;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.model.FeatureFlags;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetStatus;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.shared.GetCouponTransactionAction;
import com.ziplly.app.shared.GetCouponTransactionAction.SearchType;
import com.ziplly.app.shared.GetCouponTransactionResult;
import com.ziplly.app.shared.GetCouponsAction;
import com.ziplly.app.shared.GetCouponsResult;
import com.ziplly.app.shared.TweetAction;
import com.ziplly.app.shared.TweetResult;

public class CouponReportActivity extends AbstractActivity implements CouponReportPresenter {

  private AcceptsOneWidget panel;
  private AsyncProvider<CouponReportView> viewProvider;
  protected CouponReportView view;

  public CouponReportActivity(CachingDispatcherAsync dispatcher,
      EventBus eventBus,
      PlaceController placeController,
      CouponReportPlace place,
      ApplicationContext ctx,
      AsyncProvider<CouponReportView> viewProvider) {
    super(dispatcher, eventBus, placeController, ctx);
    this.viewProvider = viewProvider;
  }

  public static interface CouponReportView extends View<CouponReportPresenter> {

    int getCouponDataStartIndex();

    int getCouponDataPageSize();

    void displayCoupons(int start, List<CouponDTO> coupons);

    void setTotalCouponCount(Long totalCouponCount);

    void displayCouponDetails(GetCouponTransactionResult result);

    void displaySummary(TransactionSummary summary);

    void displayMessage(String message, AlertType success);

    CouponFormWidget getCouponFormWidget();
  }

  @Override
  public void start(AcceptsOneWidget panel, EventBus eventBus) {
    this.panel = panel;
    checkAccountLogin();
  }

  @Override
  protected void doStart() {
    this.viewProvider.get(new AsyncCallback<CouponReportView>() {

      @Override
      public void onFailure(Throwable caught) {
        Window.alert("Failed to load Coupon Report View");
      }

      @Override
      public void onSuccess(CouponReportView result) {
        CouponReportActivity.this.view = result;
        bind();
        setupView();
        CouponReportActivity.this.panel.setWidget(result);
        loadInitialCouponData();
      }
    });
  }

  public void loadCouponData(final int start, int pageSize) {
    GetCouponsAction action = new GetCouponsAction();
    action.setStart(start);
    action.setPageSize(pageSize);
    action.setAccountId(ctx.getAccount().getAccountId());

    dispatcher.execute(action, new DispatcherCallbackAsync<GetCouponsResult>() {

      @Override
      public void onSuccess(GetCouponsResult result) {
        view.displayCoupons(start, result.getCoupons());
        view.setTotalCouponCount(result.getTotalCouponCount());
      }
    });
  }

  @Override
  public void bind() {
    this.view.setPresenter(this);
  }

  @Override
  public void loadCouponDetails(CouponDTO coupon, int start, int pageSize) {
    GetCouponTransactionAction action = new GetCouponTransactionAction();
    action.setStart(start);
    action.setPageSize(pageSize);
    action.setSearchType(SearchType.BY_COUPON_ID);
    action.setCouponId(coupon.getCouponId());
    dispatcher.execute(action, new DispatcherCallbackAsync<GetCouponTransactionResult>() {

      @Override
      public void onSuccess(GetCouponTransactionResult result) {
        view.displayCouponDetails(result);
        displayCouponCampaignSummary(result);
      }
    });
  }

  private void displayCouponCampaignSummary(GetCouponTransactionResult result) {
    CouponTransactionSummaryCalculator calculator = new CouponTransactionSummaryCalculator();
    view.displaySummary(calculator.calculate(result));
  }

  @Override
  public void sendTweet(CouponDTO coupon) {
    // Remove it once testing is complete
    if (FeatureFlags.EnableCityCheck.isEnabled()) {
      if (!hasPermissionToPublishCoupon()) {
        view.displayMessage(stringDefinitions.couponPublishingDisabled(), AlertType.WARNING);
        return;
      }
    }
    
    Date now = new Date();
    TweetDTO tweet = new TweetDTO();
    tweet.setCoupon(coupon);
    tweet.setType(TweetType.COUPON);
    tweet.setStatus(TweetStatus.ACTIVE);
    tweet.setTimeUpdated(now);
    tweet.setTimeCreated(now);
    tweet.setSender(ctx.getAccount());
    tweet.getTargetNeighborhoods().add(ctx.getCurrentNeighborhood());
    tweet.getImages().addAll(coupon.getTweet().getImages());

    TweetAction action = new TweetAction(tweet);
    dispatcher.execute(action, new DispatcherCallbackAsync<TweetResult>() {

      @Override
      public void onSuccess(TweetResult result) {
        view.displayMessage(stringDefinitions.couponPublishedSuccessfully(), AlertType.SUCCESS);
        eventBus.fireEvent(new LoadingEventEnd());
        eventBus.fireEvent(new CouponPublishSuccessfulEvent());
      }

      @Override
      public void postHandle(Throwable th) {
        eventBus.fireEvent(new LoadingEventEnd());
      }
    });
  }

  @Override
  public void onStop() {
    view.clear();
  }

  private void setupView() {
    if (FeatureFlags.EnableCityCheck.isEnabled()) {
      if (!hasPermissionToPublishCoupon()) {
        view.displayMessage(stringDefinitions.couponPublishingDisabled(), AlertType.WARNING);
        return;
      }
    }
    initializeUploadForm(view.getCouponFormWidget().getFormUploadWidget());
  }

  private boolean hasPermissionToPublishCoupon() {
    return ctx.getCurrentNeighborhood().getCity().equalsIgnoreCase("SEATTLE");
  }

  private void loadInitialCouponData() {
    int start = view.getCouponDataStartIndex();
    int pageSize = view.getCouponDataPageSize();
    loadCouponData(start, pageSize);
  }

  @Override
  public BusinessAccountDTO getAccount() {
    return (BusinessAccountDTO) ctx.getAccount();
  }
  
  @Override
  public void go(AcceptsOneWidget container) {
  }
}
