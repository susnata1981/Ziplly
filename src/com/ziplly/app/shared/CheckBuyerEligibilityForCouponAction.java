package com.ziplly.app.shared;

import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.CouponDTO;

import net.customware.gwt.dispatch.shared.Action;

public class CheckBuyerEligibilityForCouponAction implements Action<CheckBuyerEligibilityForCouponResult> {
	private CouponDTO coupon;
	private AccountDTO buyer;

	public CheckBuyerEligibilityForCouponAction() {
  }
	
	public CheckBuyerEligibilityForCouponAction(CouponDTO coupon, AccountDTO buyer) {
		this.coupon = coupon;
		this.buyer = buyer;
  }
	
	public CouponDTO getCoupon() {
	  return coupon;
  }

	public void setCoupon(CouponDTO coupon) {
	  this.coupon = coupon;
  }

	public AccountDTO getBuyer() {
		return buyer;
	}

	public void setBuyer(AccountDTO buyer) {
		this.buyer = buyer;
	}
}
