package com.ziplly.app.server.util;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class TimeUtil {
  public static final String LOS_ANGELES_TIMEZONE = "America/Los_Angeles";
  
  public static Date toDate(Date date, String timeZoneId) {
    DateTime dateTime = new DateTime(date);
    DateTime newDate = dateTime.toDateTime(DateTimeZone.forID(timeZoneId));
    return newDate.toDate();
  }
  
  public static Date toDate(long timestamp) {
    DateTime dateTime = new DateTime(timestamp);
    long millis = dateTime.toDateTime(DateTimeZone.forID(LOS_ANGELES_TIMEZONE)).getMillis();
    return new Date(millis);
  }
  
  public static long toTimestamp(Date date, String timeZoneId) {
    DateTime dateTime = new DateTime(date);
    DateTime newDate = dateTime.toDateTime(DateTimeZone.forID(timeZoneId));
    return newDate.getMillis();
  }
}
