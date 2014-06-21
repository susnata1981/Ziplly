package com.ziplly.app.server.model.jpa;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "order_details")
public class OrderDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private Long id;
  
  @ManyToOne
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;
  
  @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinColumn(name = "item_id", nullable = false)
  private CouponItem item;
  
  private int quantity;
  
  public CouponItem getItem() {
    return item;
  }
  public void setItem(CouponItem couponItem) {
    this.item = couponItem;
  }
  public int getQuantity() {
    return quantity;
  }
  public void setQuantity(int count) {
    this.quantity = count;
  }
  public Order getOrder() {
    return order;
  }
  public void setOrder(Order order) {
    this.order = order;
  }
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
}
