package com.ziplly.app.client.view.coupon;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.model.DateRange;

public class DataBuilderTest {
  DataBuilder builder = new DataBuilder();
  
  @Test
  public void getDateRangeTest() {
    // 07/01/2014 - 10:00 AM
    DateTime now = new DateTime(2014, 7, 1, 10, 0);
    CouponDTO c1 = new CouponDTO();
    c1.setTimeCreated(now.minusDays(20).toDate());
    c1.setEndDate(now.minusDays(10).toDate());
    
    CouponDTO c2 = new CouponDTO();
    c2.setTimeCreated(now.minusDays(10).toDate());
    c2.setEndDate(now.minusDays(5).toDate());

    CouponDTO c3 = new CouponDTO();
    c3.setTimeCreated(now.minusDays(15).toDate());
    c3.setEndDate(now.minusDays(1).toDate());

    List<CouponDTO> coupons = new ArrayList<>();
    coupons.add(c1);
    coupons.add(c2);
    coupons.add(c3);
    
    DatePair dateRange = builder.getDateRange(coupons);
    Assert.assertEquals(now.minusDays(20).toDate(), dateRange.getStartDate());
  }
  
//  @Test
//  public void populateDataTest() {
//    DateTime now = new DateTime(2014, 7, 1, 10, 0);
//    CouponDTO c1 = getCoupon(5, now.minusDays(20).toDate(), now.minusDays(10).toDate());
//    CouponDTO c2 = getCoupon(6, now.minusDays(15).toDate(), now.minusDays(5).toDate());
//    CouponDTO c3 = getCoupon(7, now.minusDays(5).toDate(), now.minusDays(4).toDate());
//    List<CouponDTO> coupons = new ArrayList<>();
//    coupons.add(c1);
//    coupons.add(c2);
//    coupons.add(c3);
//    
//    DateRange dateRange = builder.getDateRange(coupons);
//    Map<DateKey, Double> sortedSalesData = new HashMap<>();
//    sortedSalesData.put(DateKey.get(now.minusDays(4).toDate()), new Double(6));
//    Map<DateKey, Double> populateData = builder.populateData(dateRange, sortedSalesData);
//    
//    assertEquals(17, populateData.size());
//  }
  
//  private CouponDTO getCoupon(double price, Date timeCreated, Date endDate) {
//    CouponDTO c1 = new CouponDTO();
//    c1.setDiscountedPrice(BigDecimal.valueOf(price));
//    c1.setTimeCreated(timeCreated);
//    c1.setEndDate(endDate);
//    return c1;
//  }
}
