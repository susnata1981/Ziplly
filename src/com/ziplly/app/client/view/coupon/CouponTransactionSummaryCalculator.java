package com.ziplly.app.client.view.coupon;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.ziplly.app.client.ZipllyController;
import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.model.CouponItemDTO;
import com.ziplly.app.model.CouponItemStatus;
import com.ziplly.app.shared.GetCouponTransactionResult;

public class CouponTransactionSummaryCalculator {
  private CommissionCalculator commissionCalculator;

  public CouponTransactionSummaryCalculator() {
    commissionCalculator = new CommissionCalculator();
  }

  public TransactionSummary calculate(Map<CouponDTO, List<CouponItemDTO>> couponTransactionMap) {
    TransactionSummary summary = new TransactionSummary();
    long totalCouponsSold = 0;
    BigDecimal totalSalesAmount = new BigDecimal(0);
    BigDecimal totalFees = new BigDecimal(0);
    long totalRedeemedCouponCount = 0;
    long totalUnusedCouponCount = 0;

    for(CouponDTO coupon : couponTransactionMap.keySet()) {
      List<CouponItemDTO> couponItems = couponTransactionMap.get(coupon);
      int transactionCount = couponItems.size();
      totalCouponsSold += transactionCount;
      totalSalesAmount = totalSalesAmount.add(BigDecimal.valueOf(transactionCount).multiply(coupon.getDiscountedPrice()));
      totalFees = totalFees.add(
          commissionCalculator.calculateFee(coupon.getDiscountedPrice()).multiply(new BigDecimal(transactionCount)));
      ZipllyController.consolelog("F = "+totalFees);
      int totalRedeemedCoupon = getRedeemCouponCount(couponItems);
      totalRedeemedCouponCount += totalRedeemedCoupon;
      totalUnusedCouponCount = totalCouponsSold -  totalRedeemedCoupon;
    }
    
    summary.setTotalCouponsSold(totalCouponsSold);
    summary.setTotalSalesAmount(totalSalesAmount);
    summary.setTotalFees(totalFees);
    summary.setTotalCouponsRedeemed(totalRedeemedCouponCount);
    summary.setTotalCouponsUnused(totalUnusedCouponCount);
    return summary;
  }

  @Deprecated
  public TransactionSummary calculate(List<CouponDTO> coupons, List<CouponItemDTO> couponItems) {
    TransactionSummary summary = new TransactionSummary();
    summary.setTotalCouponsSold(couponItems.size());
    summary.setTotalSalesAmount(getTotalSalesAmount(couponItems));
    summary.setTotalFees(new BigDecimal(0));
    return summary;
  }

  private BigDecimal getTotalSalesAmount(List<CouponItemDTO> couponItems) {
    return new BigDecimal(0);
  }

  @Deprecated
  public TransactionSummary calculate(GetCouponTransactionResult result) {
    TransactionSummary summary = new TransactionSummary();
    summary.setTotalCouponsSold(result.getTotalTransactions());
    summary.setTotalSalesAmount(getTotalSalesAmount(result));
    summary.setTotalFees(getTotalFees(result));
    long totalRedeemedCouponCount = getRedeemCouponCount(result.getPurchasedCoupons());
    long totalUnusedCouponCount = result.getTotalTransactions() - totalRedeemedCouponCount;
    summary.setTotalCouponsRedeemed(totalRedeemedCouponCount);
    summary.setTotalCouponsUnused(totalUnusedCouponCount);
    return summary;
  }

  private int getRedeemCouponCount(List<CouponItemDTO> purchasedCoupons) {
    int count = 0;
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
