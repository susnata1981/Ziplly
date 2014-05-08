package com.ziplly.app.shared;

import java.util.ArrayList;
import java.util.List;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.CouponDTO;

public class GetCouponsResult implements Result {
	private List<CouponDTO> coupons = new ArrayList<CouponDTO>();
	private Long totalCouponCount;

	public List<CouponDTO> getCoupons() {
	  return coupons;
  }

	public void setCoupons(List<CouponDTO> coupons) {
	  this.coupons = coupons;
  }

	public void setTotalCouponCount(Long totalCouponCount) {
		this.totalCouponCount = totalCouponCount;
  }
	
	public Long getTotalCouponCount() {
		return totalCouponCount;
	}
}
