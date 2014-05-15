package com.ziplly.app.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class TransactionDTO implements Serializable {
  private static final long serialVersionUID = 1L;
	private Long transactionId;
	private AccountDTO buyer;
	private String status;
	private BigDecimal amount;
	private String currency;
	private Date timeUpdated;
	private Date timeCreated;
	
	public Long getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}
	
	public AccountDTO getBuyer() {
		return buyer;
	}
	public void setBuyer(AccountDTO buyer) {
		this.buyer = buyer;
	}
	public TransactionStatus getStatus() {
		return TransactionStatus.valueOf(status);
	}
	public void setStatus(TransactionStatus status) {
		this.status = status.name();
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
	public BigDecimal getAmount() {
	  return amount;
  }
	public void setAmount(BigDecimal amount) {
	  this.amount = amount;
  }
}
