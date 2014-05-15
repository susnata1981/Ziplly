package com.ziplly.app.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="transaction")
public class Transaction extends AbstractTimestampAwareEntity {
  private static final long serialVersionUID = 1L;

  @Column(name="transaction_id")
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long transactionId;
	
	@OneToOne
	@JoinColumn(name="buyer_id")
	private Account buyer;
	
	@NotNull
	@Column(name = "status", nullable = false)
	private String status;

	@Column(name = "amount", nullable = false)
	private BigDecimal amount;
	
	@Column(name = "currency", nullable = false)
	private String currency;
	
	public Transaction() {
	}
	
	public Transaction(TransactionDTO transaction) {
		this.transactionId = transaction.getTransactionId();
//		this.buyer = new Account(transaction.getBuyer());
		this.currency = transaction.getCurrency();
		this.status = transaction.getStatus().name();
		this.amount = transaction.getAmount();
		this.timeUpdated = transaction.getTimeUpdated();
		this.timeCreated = transaction.getTimeCreated();
	}
	
	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Account getBuyer() {
		return buyer;
	}

	public void setBuyer(Account buyer) {
		this.buyer = buyer;
	}

	public TransactionStatus getStatus() {
		return TransactionStatus.valueOf(status);
	}

	public void setStatus(TransactionStatus status) {
		this.status = status.name();
	}

	public BigDecimal getAmount() {
	  return amount;
  }

	public void setAmount(BigDecimal amount) {
	  this.amount = amount;
  }
}
