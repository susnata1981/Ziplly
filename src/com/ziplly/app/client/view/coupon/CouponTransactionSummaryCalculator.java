package com.ziplly.app.client.view.coupon;

import java.math.BigDecimal;
import java.util.List;

import com.ziplly.app.model.PurchasedCouponDTO;
import com.ziplly.app.model.PurchasedCouponStatus;
import com.ziplly.app.model.TransactionStatus;
import com.ziplly.app.shared.GetCouponTransactionResult;

public class CouponTransactionSummaryCalculator {
  
  public TransactionSummary calculate(GetCouponTransactionResult result) {
    TransactionSummary summary = new TransactionSummary();
    summary.setTotalCouponsSold(result.getTotalTransactions());
    summary.setTotalSalesAmount(getTotalSalesAmount(result));
    summary.setTotalFees(getTotalFees(result));
    Long totalRedeemedCouponCount = getRedeemCouponCount(result.getPurchasedCoupons());
    Long totalUnusedCouponCount = result.getTotalTransactions() - totalRedeemedCouponCount;
    summary.setTotalCouponsRedeemed(totalRedeemedCouponCount);
    summary.setTotalCouponsUnused(totalUnusedCouponCount);
    return summary;
  }
  
  private Long getRedeemCouponCount(List<PurchasedCouponDTO> purchasedCoupons) {
    long count = 0;
    for(PurchasedCouponDTO pc : purchasedCoupons) {
      if (pc.getTransaction().getStatus() == TransactionStatus.COMPLETE && 
          pc.getStatus() == PurchasedCouponStatus.UNUSED) {
        count++;
      }
    }
    
    return count;
  }
  
  private BigDecimal getTotalSalesAmount(GetCouponTransactionResult result) {
    if (result.getTotalTransactions() > 0) {
      return 
          result
              .getPurchasedCoupons()
              .get(0)
              .getCoupon()
              .getPrice()
              .multiply(BigDecimal.valueOf(result.getTotalTransactions()));
    }
    
    return new BigDecimal(0);
  }
  
  private BigDecimal getTotalFees(GetCouponTransactionResult result) {  
    BigDecimal totalFees = new BigDecimal(0);
    
    for(PurchasedCouponDTO pc : result.getPurchasedCoupons()) {
      if (isTransactionComplete(pc)) {
        totalFees.add(getTransactionFee(pc));
      }
    }
    return totalFees;
  }
  
  private boolean isTransactionComplete(PurchasedCouponDTO pc) {
    return pc.getTransaction().getStatus() == TransactionStatus.COMPLETE;
  }
  
  private BigDecimal getTransactionFee(PurchasedCouponDTO pc) {
    if (isTransactionComplete(pc)) {
      BigDecimal commision = pc.getTransaction().getAmount().multiply(new BigDecimal(2.9/100));
      BigDecimal minimum = new BigDecimal(.30);
      if (commision.compareTo(minimum) > 0) {
        return commision;
      } else {
        return minimum;
      }
    }
    
    return new BigDecimal(0);
  }
}
