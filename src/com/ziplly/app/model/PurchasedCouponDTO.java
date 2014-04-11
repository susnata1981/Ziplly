package com.ziplly.app.model;

import java.io.Serializable;
import java.util.Date;

public class PurchasedCouponDTO implements Serializable {
  private static final long serialVersionUID = 1L;
	private Long purchasedCouponId;
	private String qrcode;
	private String status;
	private CouponTransactionDTO couponTransaction;
	private CouponDTO coupon;
	private Date timeUpdated;
	private Date timeCreated;
	
	public Long getPurchasedCouponId() {
		return purchasedCouponId;
	}
	public void setPurchasedCouponId(Long purchasedCouponId) {
		this.purchasedCouponId = purchasedCouponId;
	}
	public String getQrcode() {
		return qrcode;
	}
	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
	}
	public PurchasedCouponStatus getStatus() {
		return PurchasedCouponStatus.valueOf(status);
	}
	public void setStatus(PurchasedCouponStatus status) {
		this.status = status.name();
	}
	public CouponTransactionDTO getCouponTransaction() {
		return couponTransaction;
	}
	public void setCouponTransaction(CouponTransactionDTO couponTransaction) {
		this.couponTransaction = couponTransaction;
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
}
