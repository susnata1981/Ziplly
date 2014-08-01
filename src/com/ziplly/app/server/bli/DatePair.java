package com.ziplly.app.server.bli;

import org.joda.time.DateTime;

public class DatePair {
  private DateTime startDate;
  private DateTime endDate;

  public DatePair(DateTime startDate, DateTime endDate) {
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public DateTime getStartDate() {
    return startDate;
  }

  public void setStartDate(DateTime start) {
    this.startDate = start;
  }

  public DateTime getEndDate() {
    return endDate;
  }

  public void setEndDate(DateTime end) {
    this.endDate = end;
  }

  @Override
  public String toString() {
    return "Start: " + startDate + " End: " + endDate;
  }

}
