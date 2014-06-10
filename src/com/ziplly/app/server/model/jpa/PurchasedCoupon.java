package com.ziplly.app.server.model.jpa;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.ziplly.app.model.PurchasedCouponDTO;
import com.ziplly.app.model.PurchasedCouponStatus;
import com.ziplly.app.server.util.TimeUtil;

@Entity
@Table(name="purchased_coupon")
public class PurchasedCoupon {
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
	
	@Column(name = "time_updated")
	private long timeUpdated;
	@Column(name = "time_created", nullable = false)
	private long timeCreated;
	
	public PurchasedCoupon() {
  }

	public PurchasedCoupon(PurchasedCouponDTO prCoupon) {
		this.purchasedCouponId = prCoupon.getPurchasedCouponId();
		this.qrcode = prCoupon.getQrcode();
		this.setCoupon(new Coupon(prCoupon.getCoupon()));
		this.status = prCoupon.getStatus().name();
		this.setTransaction(new Transaction(prCoupon.getTransaction()));
		this.setTimeCreated(prCoupon.getTimeCreated());
		this.setTimeUpdated(prCoupon.getTimeUpdated());
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

  public long getTimeUpdated() {
    return timeUpdated;
  }

  public void setTimeUpdated(long timeUpdated) {
    this.timeUpdated = timeUpdated;
  }

  public void setTimeUpdated(Date timeUpdated) {
    this.timeUpdated = TimeUtil.toTimestamp(timeUpdated);
  }
  
  public long getTimeCreated() {
    return timeCreated;
  }

  public void setTimeCreated(long timeCreated) {
    this.timeCreated = timeCreated;
  }
  
  public void setTimeCreated(Date timeCreated) {
    this.timeCreated = TimeUtil.toTimestamp(timeCreated);
  }
}
