package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.CouponDTO;

public class PurchasedCouponAction implements Action<PurchaseCouponResult> { 
	private AccountDTO buyer;
	private CouponDTO coupon;
	
	public PurchasedCouponAction() {
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
