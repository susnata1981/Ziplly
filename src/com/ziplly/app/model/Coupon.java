package com.ziplly.app.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="coupon")
public class Coupon extends AbstractTimestampAwareEntity {
  private static final long serialVersionUID = 1L;
  
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="coupon_id")
	private Long couponId;
	
	private String description;
	
	@Column(name="start_date")
	private Date startDate;
	
	@Column(name="end_date")
	private Date endDate;
	
	@Column(name="coupon_price")
	private BigDecimal couponPrice;
	
	private BigDecimal price;
	
	@Column(name="item_price")
	private BigDecimal itemPrice;
	private BigDecimal discount;
	private Long quantity;
	
	@Column(name="number_allowed_per_individual")
	private int numberAllowerPerIndividual;
	
	@Column(name="quantity_purchased")
	private Long quantityPurchased;
	
	@OneToOne(mappedBy = "coupon")
	@JoinColumn(name="tweet_id")
	private Tweet tweet;
	
	public Coupon() {
  }
	
	public Coupon(CouponDTO coupon) {
		this.couponId = coupon.getCouponId();
		this.description = coupon.getDescription();
		this.startDate = coupon.getStartDate();
		this.endDate = coupon.getEndDate();
		this.couponPrice = coupon.getCouponPrice();
		this.price = coupon.getPrice();
		this.itemPrice = coupon.getItemPrice();
		this.discount = coupon.getDiscount();
		this.quantity = coupon.getQuanity();
		this.quantityPurchased = coupon.getQuantityPurchased();
		this.numberAllowerPerIndividual = coupon.getNumberAllowerPerIndividual();
		this.timeCreated = coupon.getTimeCreated();	
	}
	
	public Long getCouponId() {
		return couponId;
	}
	public void setCouponId(Long couponId) {
		this.couponId = couponId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public BigDecimal getItemPrice() {
		return itemPrice;
	}
	public void setItemPrice(BigDecimal price) {
		this.itemPrice = price;
	}
	public BigDecimal getDiscount() {
		return discount;
	}
	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}
	public Long getQuanity() {
	  return quantity;
  }
	public void setQuanity(Long quanity) {
	  this.quantity = quanity;
  }
	public Long getQuantityPurchased() {
	  return quantityPurchased;
  }
	public void setQuantityPurchased(Long quantityPurchased) {
	  this.quantityPurchased = quantityPurchased;
  }
	public BigDecimal getCouponPrice() {
	  return couponPrice;
  }
	public void setCouponPrice(BigDecimal couponPrice) {
	  this.couponPrice = couponPrice;
  }
	public BigDecimal getPrice() {
	  return price;
  }
	public void setPrice(BigDecimal price) {
	  this.price = price;
  }

	public int getNumberAllowerPerIndividual() {
	  return numberAllowerPerIndividual;
  }

	public void setNumberAllowerPerIndividual(int numberAllowerPerIndividual) {
	  this.numberAllowerPerIndividual = numberAllowerPerIndividual;
  }

	public Tweet getTweet() {
	  return tweet;
  }

	public void setTweet(Tweet tweet) {
	  this.tweet = tweet;
  }
}
