package com.ziplly.app.server.util;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Test;


public class TimeUtilTest {
  
//  @Test
//  public void toTimestampTest() {
//    DateTime dateTime = new DateTime();
//    DateTime afterTime = dateTime.plusMillis(1000);
//    long before = TimeUtil.toTimestamp(dateTime.toDate(), TimeUtil.UTC);
//    long after = TimeUtil.toTimestamp(afterTime.toDate(), TimeUtil.UTC);
//    System.out.println("B = "+ before +" after = "+after);
//    Assert.assertTrue(before< after);
//    System.out.println(TimeUtil.toDate(1401638100000L));
//    System.out.println(TimeUtil.toDate(1402329600000L));
//  }
  
//  @Test
//  public void toDateFromTimestampTest() {
//    DateTime dateTime = new DateTime();
//    long timestamp = dateTime.getMillis();
//    Date date = TimeUtil.toDate(timestamp);
//    DateTime dateTimeInPST = dateTime.toDateTime(DateTimeZone.forID(TimeUtil.UTC));
//    Assert.assertEquals(dateTimeInPST.toDate(), date);
//  }
  
  private static final String PST_TIME_ZONE = "PST8PDT";
  
  @Test
  public void convertDateTimezoneTest() {
    Date currDate = new Date();
    Date newDate = TimeUtil.toDate(currDate, DateTimeZone.UTC);
    Date oldDate = TimeUtil.toDate(newDate, DateTimeZone.forID(PST_TIME_ZONE));
    System.out.println(String.format("UTC %s, PST %s", newDate, oldDate));
    assertEquals(oldDate, currDate);
    assertEquals(newDate, oldDate);
  }
  
  @Test
  public void javaDateTest() {
    Date date = new Date();
    LocalDateTime localDateTime = new LocalDateTime(date.getTime());
    DateTime utcDateTime = new DateTime(date.getTime()).toDateTime(DateTimeZone.UTC);
//    DateTime dateTime = new DateTime(localDateTime, );
//    DateTime utcDateTime = localDateTime.toDateTime(DateTimeZone.UTC);
    DateTime pdtDateTime = utcDateTime.withZone(DateTimeZone.forID(PST_TIME_ZONE));
//    System.out.println(String.format("Local datetime %s, UTC datetime %s, PST %s", 
//        dateTime, dateTime.toDateTime(DateTimeZone.UTC), dateTime.toDateTime(DateTimeZone.forID(TimeUtil.PDT))));
// 
    System.out.println(String.format("U %s, P %s", utcDateTime, pdtDateTime));
    System.out.println(String.format("UTC time %d, PST time %d", utcDateTime.getMillis(), pdtDateTime.getMillis()));
  }
}
