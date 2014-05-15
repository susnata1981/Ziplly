package com.ziplly.app.server.bli.payment;

public class Request {
	private String name;
	private String description;
	private String price;
	private String currencyCode;
	private String paymentType;
	private String couponId;
	private String buyerId;
	private String purchaseCouponId;
	private String sellerId;
	private String subscriptionId;
	
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public String getPrice() {
		return price;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public String getCouponId() {
		return couponId;
	}
	public String getBuyerId() {
		return buyerId;
	}
	public String getPurchasedCouponId() {
		return purchaseCouponId;
	}
	
	@Override
	public String toString() {
		return name + " " + description;
	}
	public PaymentType getPaymentType() {
	  return PaymentType.valueOf(paymentType);
  }
	public void setType(PaymentType type) {
	  this.paymentType = type.name();
  }
	public String getSubscriptionId() {
	  return subscriptionId;
  }
	public void setSubscriptionId(String subscriptionId) {
	  this.subscriptionId = subscriptionId;
  }
	public String getSellerId() {
	  return sellerId;
  }
	public void setSellerId(String sellerId) {
	  this.sellerId = sellerId;
  }
}
