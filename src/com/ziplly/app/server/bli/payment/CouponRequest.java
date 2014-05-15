package com.ziplly.app.server.bli.payment;

import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.server.bli.CouponBLI;

public class CouponRequest extends BaseRequest {
	private CouponBLI couponBli;

	@Inject
	public CouponRequest(CouponBLI couponBli) {
		this.couponBli = couponBli;
  }
	
	public Long getCouponId() {
		return couponId;
	}
	public void setCouponId(Long couponId) {
		this.couponId = couponId;
	}
	public Long getBuyerId() {
		return buyerId;
	}
	public void setBuyerId(Long buyerId) {
		this.buyerId = buyerId;
	}
	public Long getPurchasedCouponId() {
		return purchasedCouponId;
	}
	public void setPurchasedCouponId(Long transactionId) {
		this.purchasedCouponId = transactionId;
	}
	
	private Long couponId;
	private Long buyerId;
	private Long purchasedCouponId;

	@Override
  public String toString() {
	  return super.toString() +  "couponId=" + couponId + ", buyerId="
	      + buyerId + ", purchasedCouponId=" + purchasedCouponId + "]";
  }
	
	@Override
  public void completeTransaction() throws DispatchException, Exception {
		couponBli.completeTransaction(purchasedCouponId);
  }
}
