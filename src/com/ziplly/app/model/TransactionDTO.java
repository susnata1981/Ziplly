package com.ziplly.app.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class TransactionDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long transactionId;

	private AccountDTO seller;
	private SubscriptionPlanDTO plan;
	private BigDecimal amount;
	private TransactionStatus status;
	// private BigDecimal recurringAmount;
	private String currencyCode;
	private Date timeUpdated;
	private Date timeCreated;

	public TransactionDTO() {
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	// public BigDecimal getRecurringAmount() {
	// return recurringAmount;
	// }
	//
	// public void setRecurringAmount(BigDecimal recurringAmount) {
	// this.recurringAmount = recurringAmount;
	// }

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public void setStatus(TransactionStatus status) {
		this.status = status;
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}

	public SubscriptionPlanDTO getPlan() {
		return plan;
	}

	public void setPlan(SubscriptionPlanDTO plan) {
		this.plan = plan;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public AccountDTO getSeller() {
		return seller;
	}

	public void setSeller(AccountDTO seller) {
		this.seller = seller;
	}

	public Date getTimeUpdated() {
		return timeUpdated;
	}

	public void setTimeUpdated(Date timeUpdated) {
		this.timeUpdated = timeUpdated;
	}
}
