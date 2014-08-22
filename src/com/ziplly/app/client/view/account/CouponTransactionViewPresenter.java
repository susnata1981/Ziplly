package com.ziplly.app.client.view.account;


public interface CouponTransactionViewPresenter {
  
  void getPurchasedCoupons(int start, int pageSize);

  void getCouponQRCodeUrl(long ordersId, long couponId);

  void cancelTransaction(long transactionId);

  void printCoupon(long id, long couponId);
}
