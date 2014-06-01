package com.ziplly.app.server.bli.payment;

public class Response {
	private String orderId;
	private String statusCode;
  public String getOrderId(){
    return this.orderId;
  }
  public String getStatusCode() {
    return statusCode;
  }
  public void setStatusCode(String statusCode) {
    this.statusCode = statusCode;
  }
}
