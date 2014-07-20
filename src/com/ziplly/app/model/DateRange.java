package com.ziplly.app.model;

import java.util.Date;

public class DateRange {
  private Date startDate;
  private Date endDate;
  public DateRange(Date startDate, Date endDate) {
    this.startDate = startDate;
    this.endDate = endDate;
  }
  
  public Date getStartDate() {
    return startDate;
  }
  public void setStartDate(Date start) {
    this.startDate = start;
  }
  public Date getEndDate() {
    return endDate;
  }
  public void setEndDate(Date end) {
    this.endDate = end;
  }
  
  @Override
  public String toString() {
    return "Start: "+startDate+" End: "+endDate;
  }
}
