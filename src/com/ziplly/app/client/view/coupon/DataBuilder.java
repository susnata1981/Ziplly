package com.ziplly.app.client.view.coupon;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.googlecode.gwt.charts.client.ColumnType;
import com.ziplly.app.client.widget.chart.ChartColumn;
import com.ziplly.app.client.widget.chart.Value;
import com.ziplly.app.model.CouponDTO;

public class DataBuilder {

  public void sortCoupons(final List<CouponDTO> coupons) {
    Collections.sort(coupons, new Comparator<CouponDTO>() {

      @Override
      public int compare(CouponDTO o1, CouponDTO o2) {
        return o1.getTimeCreated().compareTo(o2.getTimeCreated());
      }

    });
  }

  public Map<ChartColumn, Value<Double>> populateData(Map<DateKey, Double> salesPerDay, int numDays) {
    Date endDate = new Date();
    Date startDate = getLastNthDayFrom(endDate, numDays);
    DatePair datePair = new DatePair(startDate, endDate);
    Map<DateKey, Double> continuousSalesData = populateData(datePair, salesPerDay);
    // show last 30 days
    return convertToChartData(continuousSalesData);
  }
  
  private Date getLastNthDayFrom(Date endDate, int days) {
    long secondsInDay = 60 * 60 * 24 * 1000;
    long millis = endDate.getTime() - secondsInDay * days;
    Date result = new Date();
    result.setTime(millis);
    return result;
  }

  public Map<ChartColumn, Value<Double>> populateData(Map<DateKey, Double> salesPerDay, List<CouponDTO> coupons) {
    DatePair range = getDateRange(coupons);
    Map<DateKey, Double> continuousSalesData = populateData(range, salesPerDay);
    return convertToChartData(continuousSalesData);
  }

  private Map<ChartColumn, Value<Double>> convertToChartData(Map<DateKey, Double> continuousSalesData) {
    Map<ChartColumn, Value<Double>> chartData = new LinkedHashMap<ChartColumn, Value<Double>>();

    for (DateKey dateKey : continuousSalesData.keySet()) {
      ChartColumn col1 = new ChartColumn(dateKey.getKey(), ColumnType.STRING);
      chartData.put(col1, new Value<Double>(dateKey.getKey(), continuousSalesData.get(dateKey)));
    }
    
    return chartData;
  }

  DatePair getDateRange(List<CouponDTO> coupons) {
    Date startDate = null, endDate = null;
    for(CouponDTO coupon: coupons) {
      if (startDate == null || coupon.getTimeCreated().before(startDate)) {
        startDate = coupon.getTimeCreated();
      } 
      
      if (endDate == null || coupon.getEndDate().after(endDate)) {
        endDate = coupon.getEndDate();
      }
    }
    
    return new DatePair(startDate, endDate);
  }

  public Map<DateKey, Double> populateData(DatePair range, Map<DateKey, Double> sortedSalesData) {
    Map<DateKey, Double> continuousSalesDate = new LinkedHashMap<DateKey, Double>();
    Date endDate = range.getEndDate();
    for (Date currDate = range.getStartDate(); currDate.compareTo(endDate) <= 0; CalendarUtil
        .addDaysToDate(currDate, 1)) {

      DateKey dateKey = DateKey.get(currDate);
      if (sortedSalesData.containsKey(dateKey)) {
        continuousSalesDate.put(dateKey, sortedSalesData.get(dateKey));
      } else {
        continuousSalesDate.put(dateKey, new Double(0));
      }
    }

    return continuousSalesDate;
  }

  private Date min(Date... dates) {
    Date minDate = new Date();
    for (Date date : dates) {
      if (minDate.after(date)) {
        minDate = date;
      }
    }

    return minDate;
  }

  public DatePair getCouponStartEndDateRange(List<CouponDTO> coupons) {
    Date startDate = coupons.get(0).getStartDate();
    Date endDate = coupons.get(0).getEndDate();
    for (CouponDTO c : coupons) {
      if (c.getEndDate().after(endDate)) {
        endDate = c.getEndDate();
      }
    }

    Date now = new Date();
    return new DatePair(startDate, min(endDate, now));
  }

  public DatePair getCouponStartEndDateRange(CouponDTO coupon) {
    Date now = new Date();
    return new DatePair(coupon.getStartDate(), min(coupon.getEndDate(), now));
  }
}
