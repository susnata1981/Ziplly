package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class RedeemCouponAction implements Action<RedeemCouponResult>{
	private String encodedCouponCode;
	
	public RedeemCouponAction() {
  }

	public RedeemCouponAction(String encodedCouponCode) {
		this.encodedCouponCode = encodedCouponCode;
  }
	
	public String getEncodedCouponCode() {
	  return encodedCouponCode;
  }

	public void setEncodedCouponCode(String encodedCouponCode) {
	  this.encodedCouponCode = encodedCouponCode;
  }
}
