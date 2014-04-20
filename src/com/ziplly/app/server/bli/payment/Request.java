package com.ziplly.app.server.bli.payment;

public class Request {
	private String name;
	private String description;
	private String price;
	private String currencyCode;
	private String couponId;
	private String buyerId;
	private String transactionId;
	
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
	public String getTransactionId() {
		return transactionId;
	}
	
	@Override
	public String toString() {
		return name + " " + description;
	}
}
