package com.ziplly.app.server.bli.payment;

import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.server.bli.CouponBLI;

public class CouponRequestHandler extends AbstractRequestHandler {
	private CouponBLI couponBli;

	@Inject
	public CouponRequestHandler(CouponBLI couponBli) {
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
	public Long getCouponOrderId() {
		return couponOrderId;
	}
	public void setCouponOrderId(Long couponOrderId) {
		this.couponOrderId = couponOrderId;
	}
	
	private Long couponId;
	private Long buyerId;
	private Long couponOrderId;

	@Override
  public String toString() {
	  return super.toString() +  "couponId=" + couponId + ", buyerId="
	      + buyerId + ", couponOrderId=" + couponOrderId + "]";
  }
	
	@Override
  public void completeTransaction() throws DispatchException, Exception {
		couponBli.completeTransaction(couponOrderId);
  }
}
