package com.ziplly.app.server.util;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;


public class TimeUtilTest {
  
  @Test
  public void toTimestampTest() {
    DateTime dateTime = new DateTime();
    DateTime afterTime = dateTime.plusMillis(1000);
    long before = TimeUtil.toTimestamp(dateTime.toDate(), TimeUtil.LOS_ANGELES_TIMEZONE);
    long after = TimeUtil.toTimestamp(afterTime.toDate(), TimeUtil.LOS_ANGELES_TIMEZONE);
    System.out.println("B = "+ before +" after = "+after);
    Assert.assertTrue(before< after);
    System.out.println(TimeUtil.toDate(1401638100000L));
    System.out.println(TimeUtil.toDate(1402329600000L));
  }
  
  @Test
  public void toDateFromTimestampTest() {
    DateTime dateTime = new DateTime();
    long timestamp = dateTime.getMillis();
    Date date = TimeUtil.toDate(timestamp);
    DateTime dateTimeInPST = dateTime.toDateTime(DateTimeZone.forID(TimeUtil.LOS_ANGELES_TIMEZONE));
    Assert.assertEquals(dateTimeInPST.toDate(), date);
  }
}
