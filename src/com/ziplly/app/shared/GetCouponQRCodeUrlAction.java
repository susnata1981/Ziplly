package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class GetCouponQRCodeUrlAction implements Action<GetCouponQRCodeUrlResult> {
	private long couponId;
	private long orderId;
	
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

	public Long orderId() {
	  return orderId;
  }

	public void setOrderId(Long orderId) {
	  this.orderId = orderId;
  }
}
