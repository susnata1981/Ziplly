package com.ziplly.app.client.view.coupon;

import java.math.BigDecimal;
import java.util.List;

import com.google.gwt.user.client.Window;
import com.ziplly.app.model.CouponItemDTO;
import com.ziplly.app.model.CouponItemStatus;
import com.ziplly.app.shared.GetCouponTransactionResult;

public class CouponTransactionSummaryCalculator {
  private CommissionCalculator commissionCalculator;

  public CouponTransactionSummaryCalculator() {
    commissionCalculator = new CommissionCalculator();
  }
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

  private Long getRedeemCouponCount(List<CouponItemDTO> purchasedCoupons) {
    long count = 0;
    for (CouponItemDTO pc : purchasedCoupons) {
      if (pc.getStatus() == CouponItemStatus.UNUSED) {
        count++;
      }
    }

    return count;
  }

  private BigDecimal getTotalSalesAmount(GetCouponTransactionResult result) {
    if (result.getTotalTransactions() > 0) {
      return result
          .getPurchasedCoupons()
          .get(0)
          .getCoupon()
          .getDiscountedPrice()
          .multiply(BigDecimal.valueOf(result.getTotalTransactions()));
    }

    return new BigDecimal(0);
  }

  private BigDecimal getTotalFees(GetCouponTransactionResult result) {
    BigDecimal totalFees = new BigDecimal(0);
    for (CouponItemDTO pc : result.getPurchasedCoupons()) {
      totalFees = totalFees.add(commissionCalculator.calculateFee(pc.getCoupon().getDiscountedPrice()));
    }
   
    return totalFees;
  }
}
