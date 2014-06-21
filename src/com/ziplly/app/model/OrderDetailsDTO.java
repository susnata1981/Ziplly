package com.ziplly.app.model;

import java.io.Serializable;

public class OrderDetailsDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  private long id;
  private OrderDTO order;
  
  public void setId(long id) {
    this.id = id;
  }
  
  public long getId() {
    return id;
  }

  public OrderDTO getOrder() {
    return order;
  }

  public void setOrder(OrderDTO order) {
    this.order = order;
  }
}
