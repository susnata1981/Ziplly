package com.ziplly.app.client.widget.chart;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ziplly.app.client.view.coupon.DateKey;
import com.ziplly.app.model.CouponItemDTO;

public class SalesAmountLineChartBuilder extends AbstractLineChartBuilder {
  
  // Calculates sales $
  public Map<DateKey, Double> aggregateData(final List<CouponItemDTO> transactions) {
    Map<DateKey, Double> salesPerDate = new HashMap<DateKey, Double>();
    
    for(CouponItemDTO pr : transactions) {
      Date timeCreated = pr.getTimeCreated();
      DateKey dateKey = DateKey.get(timeCreated);
      if (salesPerDate.get(dateKey) == null) {
        salesPerDate.put(DateKey.get(timeCreated), new Double(0));
      }
      
      Double amount = salesPerDate.get(dateKey) + pr.getCoupon().getDiscountedPrice().doubleValue();
      salesPerDate.put(dateKey, amount);
    }
    
    return salesPerDate;
  }
}
