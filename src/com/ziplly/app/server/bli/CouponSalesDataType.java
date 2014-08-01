package com.ziplly.app.server.bli;

import java.math.BigDecimal;

public enum CouponSalesDataType {
  SALES_AMOUNT {
    @Override
    public BigDecimal getValue(BigDecimal oldValue, BigDecimal newValue) {
      return newValue.add(oldValue);
    }
  },
  SALES_VOLUME {

    @Override
    public BigDecimal getValue(BigDecimal oldValue, BigDecimal newValue) {
      return newValue.add(new BigDecimal(1));
    }
  };
  
  public abstract BigDecimal getValue(BigDecimal oldValue, BigDecimal newValue);
}
