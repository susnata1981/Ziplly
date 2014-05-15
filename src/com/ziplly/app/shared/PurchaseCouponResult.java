package com.ziplly.app.shared;

import com.ziplly.app.model.TransactionDTO;

import net.customware.gwt.dispatch.shared.Result;

public class PurchaseCouponResult implements Result {
	private TransactionDTO couponTransaction;
	
	public PurchaseCouponResult() {
  }
	
	public PurchaseCouponResult(TransactionDTO coupon) {
		this.setCouponTransaction(coupon);
  }

	public TransactionDTO getCouponTransaction() {
	  return couponTransaction;
  }

	public void setCouponTransaction(TransactionDTO couponTransaction) {
	  this.couponTransaction = couponTransaction;
  }
}
