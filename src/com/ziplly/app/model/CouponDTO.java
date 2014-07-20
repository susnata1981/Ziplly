package com.ziplly.app.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CouponDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  
	private Long couponId;
	private String title;
	private String description;
	private Date startDate;
	private Date endDate;
	private BigDecimal discountedPrice;
	private BigDecimal itemPrice;
	private Long quantity;
	private int numberAllowerPerIndividual;
	// Transient
	private List<ImageDTO> images = new ArrayList<ImageDTO>();

	private TweetDTO tweet = new TweetDTO();
	
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
		return (Date) startDate.clone();
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return (Date) endDate.clone();
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
	public Date getTimeCreated() {
	  return (Date) timeCreated.clone();
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
  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public List<ImageDTO> getImages() {
    return images;
  }
  public void setImages(List<ImageDTO> images) {
    this.images = images;
  }
  public BigDecimal getDiscountedPrice() {
    return discountedPrice;
  }
  public void setDiscountedPrice(BigDecimal discountedPrice) {
    this.discountedPrice = discountedPrice;
  }
}
