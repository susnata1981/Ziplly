package com.ziplly.app.server.util;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class TimeUtil {
//  public static final String LOS_ANGELES_TIMEZONE = "America/Los_Angeles";
  public static final String PDT = "PST8PDT";
  public static final String UTC = "UTC";
  
  /**
   * Converts given timestamp to the provided timezone.
   */
  public static Date toDate(Date date, String timeZoneId) {
    DateTime dateTime = new DateTime(date);
    DateTime newDate = dateTime.toDateTime(DateTimeZone.forID(timeZoneId));
    return newDate.toDate();
  }
  
  /**
   * Converts given timestamp to PDT timezone.
   */
  public static Date toDate(long timestamp) {
    DateTime dateTime = new DateTime(timestamp);
    DateTime newDateTime = dateTime.toDateTime(DateTimeZone.forID(PDT));
    return newDateTime.toDate();
  }
  
  /**
   * Converts to the given timezone.
   */
  public static long toTimestamp(Date date, String timeZoneId) {
    DateTime dateTime = new DateTime(date);
    DateTime newDate = dateTime.toDateTime(DateTimeZone.forID(timeZoneId));
    return newDate.getMillis();
  }
  
  /**
   * By default converts to UTC.
   */
  public static long toTimestamp(Date date) {
    DateTime dateTime = new DateTime(date);
    DateTime newDate = dateTime.toDateTime(DateTimeZone.UTC);
    return newDate.getMillis();
  }

  public static DateTime toDateTime(long timestamp) {
    return new DateTime(timestamp, DateTimeZone.forID(TimeUtil.PDT));
  }

  public static DateTime getCurrentTime() {
    return new DateTime(DateTimeZone.forID(PDT));
  }
}
