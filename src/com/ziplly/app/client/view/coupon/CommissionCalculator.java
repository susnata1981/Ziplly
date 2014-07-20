package com.ziplly.app.client.view.coupon;

import java.math.BigDecimal;

public class CommissionCalculator {
  private static BigDecimal ZIPLLY_COMMISSION = new BigDecimal(5.0 / 100);
  private static BigDecimal GOOGLE_COMMISSION = new BigDecimal(2.9 / 100);
  private static BigDecimal MIN_GOOGLE_COMMISSION = new BigDecimal(.30);
  
  public BigDecimal calculateFee(BigDecimal transactionAmount) {
    BigDecimal paymentProviderCommission = transactionAmount.multiply(GOOGLE_COMMISSION);
    BigDecimal zipllyCommission = transactionAmount.multiply(ZIPLLY_COMMISSION);
    
    BigDecimal commission = new BigDecimal(0);
    if (paymentProviderCommission.compareTo(MIN_GOOGLE_COMMISSION) > 0) {
      commission = commission.add(paymentProviderCommission);
    } else {
      commission = commission.add(MIN_GOOGLE_COMMISSION); 
    }
    
    return commission.add(zipllyCommission);
  }
}
