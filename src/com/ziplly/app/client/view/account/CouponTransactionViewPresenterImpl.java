package com.ziplly.app.client.view.account;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.places.PrintCouponPlace;
import com.ziplly.app.client.view.coupon.CouponTransactionView;
import com.ziplly.app.shared.GetCouponQRCodeUrlAction;
import com.ziplly.app.shared.GetCouponTransactionAction;

public class CouponTransactionViewPresenterImpl implements CouponTransactionViewPresenter {
  private CachingDispatcherAsync dispatcher;
  private EventBus eventBus;
  private CouponTransactionView view;

  @Inject
  public CouponTransactionViewPresenterImpl(
      CachingDispatcherAsync dispatcher,
      EventBus eventBus,
      CouponTransactionView view) {
    this.dispatcher = dispatcher;
    this.eventBus = eventBus;
    this.view = view;
  }

  @Override
  public void getPurchasedCoupons(int start, int pageSize) {
    GetCouponTransactionAction action = new GetCouponTransactionAction();
    action.setStart(start);
    action.setPageSize(pageSize);
    dispatcher.execute(action, new GetCouponTransactionHandler(eventBus, view));
  }

  @Override
  public void getCouponQRCodeUrl(long ordersId, long couponId) {
    GetCouponQRCodeUrlAction action = new GetCouponQRCodeUrlAction();
    action.setOrderId(ordersId);
    action.setCouponId(couponId);
    dispatcher.execute(action, new GetCouponQrCodeHandler(eventBus, view));
  }

  @Override
  public void cancelTransaction(long transactionId) {
    Window.alert("Cancelled transaction: " + transactionId);
  }

//  @Override
//  public void go(AcceptsOneWidget container) {
//    
//  }
//
//  @Override
//  public void bind() {
//    view.setPresenter(this);
//  }
//
//  @Override
//  public void goTo(Place place) {
//    eventBus.fireEvent(new PlaceChangeEvent(place));
//  }

  @Override
  public void printCoupon(long orderId, long couponId) {
     eventBus.fireEvent(new PlaceChangeEvent(new PrintCouponPlace(orderId, couponId)));
  }
  
  public static interface Factory {
    CouponTransactionViewPresenter create(@Assisted("couponTransactionView") CouponTransactionView view);
  }
}
