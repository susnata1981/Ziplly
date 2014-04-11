package com.ziplly.app.shared;

import com.ziplly.app.model.CouponTransactionDTO;

import net.customware.gwt.dispatch.shared.Result;

public class PurchaseCouponResult implements Result {
	private CouponTransactionDTO couponTransaction;
	
	public PurchaseCouponResult() {
  }
	
	public PurchaseCouponResult(CouponTransactionDTO coupon) {
		this.setCouponTransaction(coupon);
  }

	public CouponTransactionDTO getCouponTransaction() {
	  return couponTransaction;
  }

	public void setCouponTransaction(CouponTransactionDTO couponTransaction) {
	  this.couponTransaction = couponTransaction;
  }
}
