package com.ziplly.app.server.model.jpa;

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

import org.joda.time.DateTimeZone;

import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.server.util.TimeUtil;

@Entity
@Table(name="coupon")
public class Coupon extends AbstractEntity {
  private static final long serialVersionUID = 1L;
  
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="coupon_id")
	private Long couponId;
	
	@Column(nullable = false)
	private String title;
	
	@Column(nullable = false)
	private String description;
	
	@Column(name="item_price", nullable = false)
	private BigDecimal itemPrice;
	
	@Column(name = "discounted_price", nullable = false)
  private BigDecimal discountedPrice;
  	
	@Column(nullable = false)
	private Long quantity;
	
	@Column(name="number_allowed_per_individual")
	private int numberAllowerPerIndividual;
	
	@Column(name="quantity_purchased")
	private Long quantityPurchased;
	
	@OneToOne(mappedBy = "coupon")
	@JoinColumn(name="tweet_id")
	private Tweet tweet;

	@Column(name="start_date")
	private Date startDate;
	@Column(name = "expiration_date")
	private Date expirationDate;
	
	public Coupon() {
  }
	
	public Coupon(CouponDTO coupon) {
		this.couponId = coupon.getCouponId();
		this.setTitle(coupon.getTitle());
		this.description = coupon.getDescription();
		this.itemPrice = coupon.getItemPrice();
		this.discountedPrice = coupon.getDiscountedPrice();
		this.quantity = coupon.getQuanity();
		this.quantityPurchased = coupon.getQuantityPurchased();
		this.numberAllowerPerIndividual = coupon.getNumberAllowerPerIndividual();
		this.setStartDate(coupon.getStartDate());
		this.setExpirationDate(coupon.getEndDate());
		this.setTimeUpdated(coupon.getTimeCreated());
		this.setTimeCreated(coupon.getTimeCreated());	
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
	public BigDecimal getItemPrice() {
		return itemPrice;
	}
	public void setItemPrice(BigDecimal price) {
		this.itemPrice = price;
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

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Date getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(Date expirationDate) {
    this.expirationDate = TimeUtil.toDate(expirationDate, DateTimeZone.UTC);
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = TimeUtil.toDate(startDate, DateTimeZone.UTC);
  }

  public void incrementQuantityPurchased(int quantity) {
    this.quantityPurchased += quantity;
  }

  public BigDecimal getDiscountedPrice() {
    return discountedPrice;
  }

  public void setDiscountedPrice(BigDecimal discountedPrice) {
    this.discountedPrice = discountedPrice;
  }
  
  @Override
  public String toString() {
    return String.format("[id = %d, description = %s", couponId, title);
  }
}
