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
@Table(name="purchased_coupon")
public class PurchasedCoupon extends AbstractTimestampAwareEntity {
  private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "purchased_coupon_id")
	private Long purchasedCouponId;

	@Column(length = 512)
	private String qrcode;

	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="coupon_id")
	private Coupon coupon;
	
	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="transaction_id")
	private Transaction transaction;

	@Column(name="status", nullable = false)
	private String status;
	
	public PurchasedCoupon() {
  }

	public PurchasedCoupon(PurchasedCouponDTO prCoupon) {
		this.purchasedCouponId = prCoupon.getPurchasedCouponId();
		this.qrcode = prCoupon.getQrcode();
		this.setCoupon(new Coupon(prCoupon.getCoupon()));
		this.status = prCoupon.getStatus().name();
		this.setTransaction(new Transaction(prCoupon.getTransaction()));
		this.timeCreated = prCoupon.getTimeCreated();
		this.timeUpdated = prCoupon.getTimeUpdated();
  }

	public Long getPurchasedCouponId() {
		return purchasedCouponId;
	}

	public void setPurchasedCouponId(Long purchasedCouponId) {
		this.purchasedCouponId = purchasedCouponId;
	}

	public String getQrcode() {
		return qrcode;
	}

	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
	}

	public PurchasedCouponStatus getStatus() {
		return PurchasedCouponStatus.valueOf(status);
	}

	public void setStatus(PurchasedCouponStatus status) {
		this.status = status.name();
	}

	public Transaction getCouponTransaction() {
		return getTransaction();
	}

	public void setCouponTransaction(Transaction couponTransaction) {
		this.setTransaction(couponTransaction);
	}

	public Coupon getCoupon() {
	  return coupon;
  }

	public void setCoupon(Coupon coupon) {
	  this.coupon = coupon;
  }

	public Transaction getTransaction() {
	  return transaction;
  }

	public void setTransaction(Transaction transaction) {
	  this.transaction = transaction;
  }
}
