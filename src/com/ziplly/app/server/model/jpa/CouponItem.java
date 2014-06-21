package com.ziplly.app.server.model.jpa;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.ziplly.app.model.CouponItemDTO;
import com.ziplly.app.model.CouponItemStatus;

@Entity
@Table(name="coupon_item")
public class CouponItem extends AbstractEntity {
  private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@Column(length = 512)
	private String qrcode;

	@OneToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name="coupon_id")
	private Coupon coupon;

	@OneToOne
	@JoinColumn(name = "order_details_id")
	private OrderDetails orderDetails;
	
	@Column(name="status", nullable = false)
	private String status;
	
	public CouponItem() {
  }

	public CouponItem(CouponItemDTO prCoupon) {
		this.setId(prCoupon.getCouponItemId());
		this.qrcode = prCoupon.getQrcode();
		this.setCoupon(new Coupon(prCoupon.getCoupon()));
		this.status = prCoupon.getStatus().name();
		this.setTimeCreated(prCoupon.getTimeCreated());
		this.setTimeUpdated(prCoupon.getTimeUpdated());
		this.setOrderDetails(orderDetails);
  }

	public Long getCouponItemId() {
		return getId();
	}

	public void setCouponItemId(Long couponItemId) {
		this.setId(couponItemId);
	}

	public String getQrcode() {
		return qrcode;
	}

	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
	}

	public CouponItemStatus getStatus() {
		return CouponItemStatus.valueOf(status);
	}

	public void setStatus(CouponItemStatus status) {
		this.status = status.name();
	}

	public Coupon getCoupon() {
	  return coupon;
  }

	public void setCoupon(Coupon coupon) {
	  this.coupon = coupon;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public OrderDetails getOrderDetails() {
    return orderDetails;
  }

  public void setOrderDetails(OrderDetails orderDetails) {
    this.orderDetails = orderDetails;
  }
}
