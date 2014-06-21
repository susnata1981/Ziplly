package com.ziplly.app.model;

import java.io.Serializable;

public class OrderDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  private long id;

  public void setId(long id) {
    this.id = id;
  }

  public long getId() {
    return id;
  }
}
