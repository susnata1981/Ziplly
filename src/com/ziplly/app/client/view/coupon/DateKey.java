package com.ziplly.app.client.view.coupon;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;

public class DateKey implements Comparable<DateKey> {
  private static DateTimeFormat formatter = DateTimeFormat.getFormat(PredefinedFormat.YEAR_MONTH_DAY);
  private String key;
  
  private DateKey(String key) {
    this.key = key;
  }
  
  public static DateKey get(Date date) {
//    ZipllyController.consolelog("D = "+formatter.format(date));
    return new DateKey(formatter.format(date));
  }

  public Date date() {
    return formatter.parse(key);
  }
  
  public String getKey() {
    return key;
  }

  @Override
  public int compareTo(DateKey o) {
    return this.key.compareTo(o.key);
  }
  
  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    
    if (!(o instanceof DateKey)) {
      return false;
    }
    
    DateKey d = (DateKey)o;
    
    return key.equals(d.key);
  }
  
  @Override
  public int hashCode() {
    return key.hashCode();
  }
}
