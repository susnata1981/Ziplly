package com.ziplly.app.client.view.coupon;

import java.math.BigDecimal;

public class TransactionSummary {
  private BigDecimal totalSalesAmount;
  private BigDecimal totalFees;
  private long totalCouponsSold;
  private long totalCouponsRedeemed;
  private long totalCouponsUnused;
  
  public BigDecimal getTotalSalesAmount() {
    return totalSalesAmount;
  }

  public void setTotalSalesAmount(BigDecimal totalSales) {
    this.totalSalesAmount = totalSales;
  }

  public long getTotalCouponsRedeemed() {
    return totalCouponsRedeemed;
  }

  public void setTotalCouponsRedeemed(long totalCouponsRedeemed) {
    this.totalCouponsRedeemed = totalCouponsRedeemed;
  }

  public long getTotalCouponsSold() {
    return totalCouponsSold;
  }

  public void setTotalCouponsSold(long totalCouponsSold) {
    this.totalCouponsSold = totalCouponsSold;
  }

  public long getTotalCouponsUnused() {
    return totalCouponsUnused;
  }

  public void setTotalCouponsUnused(long totalCouponsUnused) {
    this.totalCouponsUnused = totalCouponsUnused;
  }

  public BigDecimal getTotalFees() {
    return totalFees;
  }

  public void setTotalFees(BigDecimal totalFees) {
    this.totalFees = totalFees;
  }
}