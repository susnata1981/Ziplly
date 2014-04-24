package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class GetCouponQRCodeUrlAction implements Action<GetCouponQRCodeUrlResult> {
	private Long couponId;
	private Long couponTransactionId;
	
	public GetCouponQRCodeUrlAction() {
  }
	
	public GetCouponQRCodeUrlAction(Long couponId) {
		this.setCouponId(couponId);
  }

	public Long getCouponId() {
	  return couponId;
  }

	public void setCouponId(Long couponId) {
	  this.couponId = couponId;
  }

	public Long getCouponTransactionId() {
	  return couponTransactionId;
  }

	public void setCouponTransactionId(Long couponTransactionId) {
	  this.couponTransactionId = couponTransactionId;
  }
}
