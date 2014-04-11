package com.ziplly.app.model;

import java.io.Serializable;
import java.util.Date;

public class CouponTransactionDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  
	private Long transactionId;
	private CouponDTO coupon;
	private AccountDTO buyer;
	private PurchasedCouponDTO purchasedCoupon;
	private TransactionStatus status;
	private String type;
	private String currency;
	private Date timeUpdated;
	private Date timeCreated;
	
	public Long getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
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
	public PurchasedCouponDTO getPurchasedCoupon() {
		return purchasedCoupon;
	}
	public void setPurchasedCoupon(PurchasedCouponDTO purchasedCoupon) {
		this.purchasedCoupon = purchasedCoupon;
	}
	public TransactionStatus getStatus() {
		return status;
	}
	public void setStatus(TransactionStatus status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public Date getTimeCreated() {
	  return timeCreated;
  }
	public void setTimeCreated(Date timeCreated) {
	  this.timeCreated = timeCreated;
  }
	public Date getTimeUpdated() {
	  return timeUpdated;
  }
	public void setTimeUpdated(Date timeUpdated) {
	  this.timeUpdated = timeUpdated;
  }
}
