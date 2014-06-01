package com.ziplly.app.client.view.coupon;

import java.math.BigDecimal;

public class TransactionSummary {
  private BigDecimal totalSalesAmount;
  private BigDecimal totalFees;
  private Long totalCouponsSold;
  private Long totalCouponsRedeemed;
  private Long totalCouponsUnused;
  
  public BigDecimal getTotalSalesAmount() {
    return totalSalesAmount;
  }

  public void setTotalSalesAmount(BigDecimal totalSales) {
    this.totalSalesAmount = totalSales;
  }

  public Long getTotalCouponsRedeemed() {
    return totalCouponsRedeemed;
  }

  public void setTotalCouponsRedeemed(Long totalCouponsRedeemed) {
    this.totalCouponsRedeemed = totalCouponsRedeemed;
  }

  public Long getTotalCouponsSold() {
    return totalCouponsSold;
  }

  public void setTotalCouponsSold(Long totalCouponsSold) {
    this.totalCouponsSold = totalCouponsSold;
  }

  public Long getTotalCouponsUnused() {
    return totalCouponsUnused;
  }

  public void setTotalCouponsUnused(Long totalCouponsUnused) {
    this.totalCouponsUnused = totalCouponsUnused;
  }

  public BigDecimal getTotalFees() {
    return totalFees;
  }

  public void setTotalFees(BigDecimal totalFees) {
    this.totalFees = totalFees;
  }
}