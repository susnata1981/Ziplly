package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.CouponDTO;

public class GetCouponQRCodeUrlResult implements Result {
	private String url;
	private CouponDTO coupon;
	private AccountDTO account;
	
	public GetCouponQRCodeUrlResult() {
  }
	
	public GetCouponQRCodeUrlResult(String url) {
		this.setUrl(url);
  }

	public String getUrl() {
	  return url;
  }

	public void setUrl(String url) {
	  this.url = url;
  }

	public CouponDTO getCoupon() {
	  return coupon;
  }

	public void setCoupon(CouponDTO coupon) {
	  this.coupon = coupon;
  }

	public void setSeller(AccountDTO account) {
		this.account = account;
  }

	public AccountDTO getSeller() {
		return account;
  }
}
