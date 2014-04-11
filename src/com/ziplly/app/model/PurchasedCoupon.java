package com.ziplly.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="purchased_coupon")
public class PurchasedCoupon extends AbstractTimestampAwareEntity {
  private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long purchasedCouponId;

	@Column(length = 512)
	private String qrcode;

	@OneToOne(mappedBy="purchasedCoupon")
	private CouponTransaction couponTransaction;

	@Column(name="status")
	private String status;
	
	public PurchasedCoupon() {
  }

	public PurchasedCoupon(PurchasedCouponDTO coupon) {
		this.purchasedCouponId = coupon.getPurchasedCouponId();
		this.qrcode = coupon.getQrcode();
		this.status = coupon.getStatus().name();
		this.couponTransaction = new CouponTransaction(coupon.getCouponTransaction());
		this.timeCreated = coupon.getTimeCreated();
		this.timeUpdated = coupon.getTimeUpdated();
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

	public CouponTransaction getCouponTransaction() {
		return couponTransaction;
	}

	public void setCouponTransaction(CouponTransaction couponTransaction) {
		this.couponTransaction = couponTransaction;
	}
}
