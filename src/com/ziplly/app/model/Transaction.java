package com.ziplly.app.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@NamedQueries({
	@NamedQuery(
		name = "findTransactionByAccountAndDate",
		query = "from Transaction t where to_char(t.timeCreated,'MM-YYYY') = :monthYear"
	),
	@NamedQuery(
		name = "findTransactionByAccount",
		query = "from Transaction t where t.seller.accountId = :accountId and t.status != :status"
	)
})
@Entity
@Table(name="transaction")
public class Transaction {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="transaction_id")
	private Long transactionId;
	
	@OneToOne
	@JoinColumn(name="account_id")
	private Account seller;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="subscription_id")
	private SubscriptionPlan plan;
	
	private BigDecimal amount;
//	private BigDecimal recurringAmount;
	
	private TransactionStatus status;
	private String currencyCode;
	
	@Column(name="time_updated")
	private Date timeUpdated;

	@Column(name="time_created")
	private Date timeCreated;
	
	public Transaction() {
	}
	
	public Transaction(TransactionDTO txn) {
		if (txn != null) {
//			transactionId = txn.getTransactionId();
			seller = new Account(txn.getSeller());
			plan = new SubscriptionPlan(txn.getPlan());
			amount = txn.getAmount();
			currencyCode = txn.getCurrencyCode();
			status = txn.getStatus();
			timeCreated = txn.getTimeCreated();
		}
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
//	public BigDecimal getRecurringAmount() {
//		return recurringAmount;
//	}
//	public void setRecurringAmount(BigDecimal recurringAmount) {
//		this.recurringAmount = recurringAmount;
//	}
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

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public Account getSeller() {
		return seller;
	}

	public void setSeller(Account seller) {
		this.seller = seller;
	}

	public SubscriptionPlan getPlan() {
		return plan;
	}

	public void setPlan(SubscriptionPlan plan) {
		this.plan = plan;
	}
}
