package com.ziplly.app.model;

import java.io.Serializable;
import java.util.Date;

public class CouponItemDTO implements Serializable {
  private static final long serialVersionUID = 1L;
	private Long couponItemId;
	private String qrcode;
	private String status;
	private CouponDTO coupon;
	private OrderDetailsDTO orderDetails;
	private Date timeUpdated;
	private Date timeCreated;
	
	public Long getCouponItemId() {
		return couponItemId;
	}
	public void setCouponItemId(Long purchasedCouponId) {
		this.couponItemId = purchasedCouponId;
	}
	public String getQrcode() {
		return qrcode;
	}
	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
	}
	public CouponItemStatus getStatus() {
		return CouponItemStatus.valueOf(status);
	}
	public void setStatus(CouponItemStatus status) {
		this.status = status.name();
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public Date getTimeUpdated() {
	  return timeUpdated;
  }
	public void setTimeUpdated(Date timeUpdated) {
	  this.timeUpdated = timeUpdated;
  }
	public Date getTimeCreated() {
	  return timeCreated;
  }
	public void setTimeCreated(Date timeCreated) {
	  this.timeCreated = timeCreated;
  }
	public CouponDTO getCoupon() {
	  return coupon;
  }
	public void setCoupon(CouponDTO coupon) {
	  this.coupon = coupon;
  }
  public OrderDetailsDTO getOrderDetails() {
    return orderDetails;
  }
  public void setOrderDetails(OrderDetailsDTO orderDetails) {
    this.orderDetails = orderDetails;
  }
}
