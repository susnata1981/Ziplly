package com.ziplly.app.shared;

import java.util.ArrayList;
import java.util.List;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.model.PurchasedCouponDTO;

public class GetCouponTransactionResult implements Result {
  private CouponDTO coupon;
	private List<PurchasedCouponDTO> transactions = new ArrayList<PurchasedCouponDTO>();
	private Long totalTransactions;
	
	public List<PurchasedCouponDTO> getPurchasedCoupons() {
	  return transactions;
  }

	public void setPurchasedCoupons(List<PurchasedCouponDTO> transactions) {
	  this.transactions = transactions;
  }

	public Long getTotalTransactions() {
	  return totalTransactions;
  }

	public void setTotalTransactions(Long totalTransactions) {
	  this.totalTransactions = totalTransactions;
  }

 public CouponDTO getCoupon() {
  return coupon;
 }

 public void setCoupon(CouponDTO coupon) {
  this.coupon = coupon;
 }
}
