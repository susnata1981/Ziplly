package com.ziplly.app.shared;

import com.ziplly.app.model.CouponDTO;

import net.customware.gwt.dispatch.shared.Action;

public class CheckBuyerEligibilityForCouponAction implements Action<CheckBuyerEligibilityForCouponResult> {
	private CouponDTO coupon;

	public CheckBuyerEligibilityForCouponAction() {
  }
	
	public CheckBuyerEligibilityForCouponAction(CouponDTO coupon) {
		this.coupon = coupon;
  }
	
	public CouponDTO getCoupon() {
	  return coupon;
  }

	public void setCoupon(CouponDTO coupon) {
	  this.coupon = coupon;
  }
}
