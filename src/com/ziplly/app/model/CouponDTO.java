package com.ziplly.app.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class CouponDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  
	private Long couponId;
	private String description;
	private Date startDate;
	private Date endDate;
	private BigDecimal couponPrice;
	private BigDecimal price;
	private BigDecimal itemPrice;
	private BigDecimal discount;
	private Long quantity;
	private int numberAllowerPerIndividual;
	private TweetDTO tweet;
	
	private Long quantityPurchased;
	private Date timeCreated;
	
	private String jwtToken;
	
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
	public Date getTimeCreated() {
	  return timeCreated;
  }
	public void setTimeCreated(Date timeCreated) {
	  this.timeCreated = timeCreated;
  }
	public TweetDTO getTweet() {
	  return tweet;
  }
	public void setTweet(TweetDTO tweet) {
	  this.tweet = tweet;
  }
	public String getJwtToken() {
	  return jwtToken;
  }
	public void setJwtToken(String jwtToken) {
	  this.jwtToken = jwtToken;
  }
	public int getNumberAllowerPerIndividual() {
	  return numberAllowerPerIndividual;
  }
	public void setNumberAllowerPerIndividual(int numberAllowerPerIndividual) {
	  this.numberAllowerPerIndividual = numberAllowerPerIndividual;
  }
}