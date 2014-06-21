package com.ziplly.app.server.bli.payment;

import net.customware.gwt.dispatch.shared.DispatchException;

public abstract class AbstractRequestHandler {
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getOrderId() {
	  return orderId;
  }
	public void setOrderId(String orderId) {
	  this.orderId = orderId;
  }
	private String name;
	private String description;
	private String price;
	private String currencyCode;
	private String orderId;
	
	@Override
  public String toString() {
	  return "BaseRequest [name=" + name + ", description=" + description + ", price=" + price
	      + ", currencyCode=" + currencyCode + ", orderId=" + orderId + "]";
  }
	
	public abstract void completeTransaction() throws NumberFormatException, DispatchException, Exception;
}
