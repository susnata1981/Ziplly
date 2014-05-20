package com.ziplly.app.client.widget.chart;

public enum ChartType {
  SALES_VOLUME("Sales Volume", "Day", "Coupons sold") { 
    public AbstractLineChartBuilder getAbstractLineChartBuilder() {
      return new SalesVolumeLineChartBuilder(); 
    }
  },
  SALES_AMOUNT("Sales $", "Day", "$") { 
      public AbstractLineChartBuilder getAbstractLineChartBuilder() {
        return new SalesAmountLineChartBuilder(); 
      }
  };
  
  public abstract AbstractLineChartBuilder getAbstractLineChartBuilder();
  
  private String title;
  private String yAxisTitle;
  private String xAxisTitle;

  private ChartType(String title, String xAxisTitle, String yAxisTitle) {
    this.title = title;
    this.setxAxisTitle(xAxisTitle);
    this.setyAxisTitle(yAxisTitle);
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
  public String getYAxisTitle() {
    return yAxisTitle;
  }
  public void setyAxisTitle(String yAxisTitle) {
    this.yAxisTitle = yAxisTitle;
  }
  public String getXAxisTitle() {
    return xAxisTitle;
  }
  public void setxAxisTitle(String xAxisTitle) {
    this.xAxisTitle = xAxisTitle;
  }
}
