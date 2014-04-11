package com.ziplly.app.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="coupon_transaction")
public class CouponTransaction extends AbstractTimestampAwareEntity {
  private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long transactionId;
	
	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="coupon_id")
	private Coupon coupon;
	
	@OneToOne
	@JoinColumn(name="buyer_id")
	private Account buyer;
	
	@OneToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name="purchased_coupon_id")
	private PurchasedCoupon purchasedCoupon;
	
	@Column(name="status")
	private String status;
	// What is this for?
	private String type;
	private String currency;
	
	public CouponTransaction() {
	}
	
	public CouponTransaction(CouponTransactionDTO coupon) {
		this.transactionId = coupon.getTransactionId();
		this.coupon = new Coupon(coupon.getCoupon());
		this.buyer = new Account(coupon.getBuyer());
		this.purchasedCoupon = new PurchasedCoupon(coupon.getPurchasedCoupon());
		this.currency = coupon.getCurrency();
		this.type = coupon.getType();
		this.timeUpdated = coupon.getTimeUpdated();
		this.timeCreated = coupon.getTimeCreated();
	}
	
	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public Coupon getCoupon() {
		return coupon;
	}

	public void setCoupon(Coupon coupon) {
		this.coupon = coupon;
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

	public PurchasedCoupon getPurchasedCoupon() {
	  return purchasedCoupon;
  }

	public void setPurchasedCoupon(PurchasedCoupon purchasedCoupon) {
	  this.purchasedCoupon = purchasedCoupon;
  }
}
