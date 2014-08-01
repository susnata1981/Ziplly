package com.ziplly.app.server.bli;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.joda.time.DateTime;

import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.model.CouponItemDTO;

public class CouponSalesDataBuilder {

  public void sortCoupons(final List<CouponDTO> coupons) {
    Collections.sort(coupons, new Comparator<CouponDTO>() {

      @Override
      public int compare(CouponDTO o1, CouponDTO o2) {
        return o1.getTimeCreated().compareTo(o2.getTimeCreated());
      }

    });
  }

  private static Comparator<CouponDTO> COUPON_COMPARATOR = new Comparator<CouponDTO>() {

    @Override
    public int compare(CouponDTO o1, CouponDTO o2) {
      return o1.getTimeCreated().compareTo(o2.getTimeCreated());
    }
    
  };
  
  public Map<String, BigDecimal> getSalesData(
      CouponSalesDataType dataType, 
      int days, 
      Map<CouponDTO, List<CouponItemDTO>> couponTransactionMap) {
    
    DateTime endDate = new DateTime();
    DateTime startDate = endDate.minusDays(days);
    
    TreeMap<CouponDTO, List<CouponItemDTO>> sortedCouponTransactionMap = new TreeMap<CouponDTO, List<CouponItemDTO>>(COUPON_COMPARATOR);
    
    Map<String, BigDecimal> salesData = cannonizeData(dataType, sortedCouponTransactionMap);
    
    return populateData(startDate, endDate, salesData);
  }
  
  private Map<String, BigDecimal> populateData(
      DateTime startDate,
      DateTime endDate,
      Map<String, BigDecimal> salesData) {
    
    Map<String, BigDecimal> continuousSalesDate = new LinkedHashMap<String, BigDecimal>();

    for(DateTime temp = startDate; temp.isBefore(endDate); temp = temp.plusDays(1)) {
      String dateKey = getDateKey(temp.toDate());

      if (salesData.containsKey(dateKey)) {
        continuousSalesDate.put(dateKey, salesData.get(dateKey));
      } else {
        continuousSalesDate.put(dateKey, new BigDecimal(0));
      }
    }
    
    return continuousSalesDate;
  }

  private Map<String, BigDecimal> cannonizeData(
      CouponSalesDataType dataType, TreeMap<CouponDTO, List<CouponItemDTO>> couponTransactionMap) {
    
    Map<String, BigDecimal> salesPerDate = new LinkedHashMap<String, BigDecimal>();

    for (List<CouponItemDTO> couponItems : couponTransactionMap.values()) {
      for (CouponItemDTO pr : couponItems) {
        String dateKey = getDateKey(pr.getTimeCreated());
        if (!salesPerDate.containsKey(dateKey)) {
          salesPerDate.put(dateKey, new BigDecimal(0));
        }

        BigDecimal value = salesPerDate.get(dateKey);
        dataType.getValue(value, pr.getCoupon().getDiscountedPrice());
      }
    }
    
    return salesPerDate;
  }

  private String getDateKey(Date timeCreated) {
    DateTime time = new DateTime(timeCreated);
    return String.format("%d/%d/%d", time.getMonthOfYear(), time.getDayOfMonth(), time.getYear());
  }

//  public Map<ChartColumn, Value<Double>> populateData(Map<DateKey, Double> salesPerDay, List<CouponDTO> coupons) {
//    DatePair range = getDateRange(coupons);
//    Map<DateKey, Double> continuousSalesData = populateData(range, salesPerDay);
//    // show last 30 days
////    getLastNDaysData(continuousSalesData, 30);
//    return convertToChartData(continuousSalesData);
//  }

//  private Map<ChartColumn, Value<Double>> convertToChartData(Map<DateKey, Double> continuousSalesData) {
//    Map<ChartColumn, Value<Double>> chartData = new LinkedHashMap<ChartColumn, Value<Double>>();
//
//    for (DateKey dateKey : continuousSalesData.keySet()) {
//      ChartColumn col1 = new ChartColumn(dateKey.getKey(), ColumnType.NUMBER);
//      chartData.put(col1, new Value<Double>(dateKey.getKey(), continuousSalesData.get(dateKey)));
//    }
//    
//    return chartData;
//  }

//  DatePair getDateRange(int days, Iterable<CouponDTO> coupons) {
//    Date startDate = null, endDate = null;
//    for(CouponDTO coupon: coupons) {
//      if (startDate == null || coupon.getTimeCreated().before(startDate)) {
//        startDate = coupon.getTimeCreated();
//      } 
//      
//      if (endDate == null || coupon.getEndDate().after(endDate)) {
//        endDate = coupon.getEndDate();
//      }
//    }
//    
//    DateTime now = new DateTime();
//    DateTime startingDate = new DateTime(startDate).minus(days);
//    startingDate = min(startingDate, new DateTime(startDate));
//    DateTime endingDate = min(now, new DateTime(endDate));
//    
//    return new DatePair(startingDate, endingDate);
//  }

//  public Map<DateKey, Double> populateData(DatePair range, Map<DateKey, Double> sortedSalesData) {
//
//    Map<DateKey, Double> continuousSalesDate = new LinkedHashMap<DateKey, Double>();
//    Date now = new Date();
//    Date endDate = min(now, range.getEndDate());
//    for (Date currDate = range.getStartDate(); currDate.compareTo(endDate) <= 0; CalendarUtil
//        .addDaysToDate(currDate, 1)) {
//
//      DateKey dateKey = DateKey.get(currDate);
//
//      if (sortedSalesData.containsKey(dateKey)) {
//        continuousSalesDate.put(dateKey, sortedSalesData.get(dateKey));
//      } else {
//        continuousSalesDate.put(dateKey, new Double(0));
//      }
//    }
//
//    return continuousSalesDate;
//  }

  private Date min(Date... dates) {
    Date minDate = new Date();
    for (Date date : dates) {
      if (minDate.after(date)) {
        minDate = date;
      }
    }

    return minDate;
  }

  private DateTime min(DateTime d1, DateTime d2) {
    return d1.compareTo(d2) > 0 ? d1 : d2;
  }

//  public DatePair getCouponStartEndDateRange(List<CouponDTO> coupons) {
//    Date startDate = coupons.get(0).getStartDate();
//    Date endDate = coupons.get(0).getEndDate();
//    for (CouponDTO c : coupons) {
//      if (c.getEndDate().after(endDate)) {
//        endDate = c.getEndDate();
//      }
//    }
//
//    Date now = new Date();
//    return new DatePair(startDate, min(endDate, now));
//  }
//
//  public DatePair getCouponStartEndDateRange(CouponDTO coupon) {
//    Date now = new Date();
//    return new DatePair(coupon.getStartDate(), min(coupon.getEndDate(), now));
//  }
}
