package com.ziplly.app.client.widget.chart;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ziplly.app.model.CouponItemDTO;
import com.ziplly.app.shared.GetCouponTransactionResult;

public class SalesAmountLineChartBuilder extends AbstractLineChartBuilder {
  
  // Calculates sales $
  public Map<Date, Double> aggregateData(final GetCouponTransactionResult result) {
    Map<Date, Double> salesPerDate = new HashMap<Date, Double>();
    
    if (result.getPurchasedCoupons().size() == 0) {
      Date startDate = result.getCoupon().getStartDate();
      Date endDate = new Date();
      return createNoDataSet(startDate, endDate);
    }
    
    for(CouponItemDTO pr : result.getPurchasedCoupons()) {
//      if (pr.getTransaction().getStatus() != TransactionStatus.ACTIVE) {
//        continue;
//      }
      
      Date timeCreated = pr.getTimeCreated();
      if (salesPerDate.get(timeCreated) == null) {
        salesPerDate.put(timeCreated, new Double(0));
      }
      
      Double amount = salesPerDate.get(timeCreated) + pr.getCoupon().getDiscountedPrice().doubleValue();
      salesPerDate.put(timeCreated, amount);
    }
    
    return salesPerDate;
  }
}
